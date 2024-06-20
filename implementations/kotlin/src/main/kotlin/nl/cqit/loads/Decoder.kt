@file:OptIn(ExperimentalUnsignedTypes::class, ExperimentalStdlibApi::class)

package nl.cqit.loads

import nl.cqit.loads.utils.toUByteArray
import kotlin.reflect.*
import kotlin.reflect.full.primaryConstructor
import kotlin.text.Charsets.UTF_8

private val ARRAY_START: UByte = 0xFAu
private val BINARY_VALUE: UByte = 0xFBu
private val OBJECT_START: UByte = 0xFCu
private val NULL_VALUE: UByte = 0xFDu
private val CONTAINER_END: UByte = 0xFEu
private val ELEMENT_SEPARATOR: UByte = 0xFFu

private val SPECIAL_BYTES = ubyteArrayOf(
    ARRAY_START,
    BINARY_VALUE,
    OBJECT_START,
    NULL_VALUE,
    CONTAINER_END,
    ELEMENT_SEPARATOR
)
private val VALUE_TERMINATORS = ubyteArrayOf(ELEMENT_SEPARATOR, CONTAINER_END)

private fun elemType(type: KType) = type.arguments[0].type!!
private fun keyType(type: KType) = type.arguments[0].type!!
private fun valueType(type: KType) = type.arguments[1].type!!

data class Obj(
    val map: Map<String, String>,
    val arr: List<String>
)

fun main() {
    val data = ubyteArrayOf(
        0xFCu,
        *"map".toUByteArray(UTF_8),
        0xFFu,
        0xFCu,
        0x34u, 0x35u, 0x36u,
        0xFFu,
        0x37u, 0x38u, 0x39u,
        0xFEu,
        0xFFu,
        *"arr".toUByteArray(UTF_8),
        0xFFu,
        0xFAu,
        0x31u, 0x32u, 0x33u,
        0xFFu,
        0x34u, 0x35u, 0x36u,
        0xFEu,
        0xFEu
    )
    val result: Obj? = decode(data)
    println(result)
}

inline fun <reified T : Any> decode(data: UByteArray): T? {
    val type = typeOf<T>()
    return decode(type, data, 0).second as T?
}

fun decode(type: KType, data: UByteArray, offset: Int): Pair<Int, Any?> {
    val (newOffset: Int, result: Any) = when {
        type.classifier == Array::class -> toArray(type, data, offset)
        type.classifier == List::class -> toList(type, data, offset)
        type.classifier == Set::class -> toSet(type, data, offset)
        type.classifier == Collection::class -> toCollection(type, data, offset)
        type.classifier == Map::class -> toMap(type, data, offset)
        type.classifier == String::class -> toString(data, offset)
        isData(type.classifier) -> toData(type, data, offset)
//        ByteArray::class -> TODO()
//        UByteArray::class -> TODO()
//        String::class -> TODO()
//        Byte::class -> TODO()
//        Short::class -> TODO()
//        Int::class -> TODO()
//        Long::class -> TODO()
//        UByte::class -> TODO()
//        UShort::class -> TODO()
//        UInt::class -> TODO()
//        ULong::class -> TODO()
//        Float::class -> TODO()
//        Double::class -> TODO()
//        Boolean::class -> TODO()
//        Instant::class -> TODO()
//        OffsetDateTime::class -> TODO()
//        ZonedDateTime::class -> TODO()
        else -> throw NotImplementedError()
    }
    return newOffset to result
}

fun isData(classifier: KClassifier?): Boolean =
    classifier is KClass<*> && classifier.isData

fun toString(data: UByteArray, offset: Int): Pair<Int, String> {
    var i = offset
    while (i < data.size && data[i] !in VALUE_TERMINATORS) i++
    val slice = data.sliceArray(offset until i)
    val specialCharactersInString = slice.intersect(SPECIAL_BYTES)
    if (specialCharactersInString.isNotEmpty()) {
        val invalidCharacters = specialCharactersInString.map {
            val hex = it.toHexString(HexFormat.UpperCase)
            val pos = slice.indexOf(it) + offset
            "Ox$hex at position $pos"
        }
        throw IllegalArgumentException(
            "Invalid character(s) found in string value: ${invalidCharacters.joinToString()}"
        )
    }
    return i to String(slice.toByteArray())
}

fun toArray(elementType: KType, data: UByteArray, offset: Int): Pair<Int, Array<Any?>> =
    toArrayContainer(elementType, data, offset, MutableList<Any?>::toTypedArray)

fun toList(elementType: KType, data: UByteArray, offset: Int): Pair<Int, List<Any?>> =
    toArrayContainer(elementType, data, offset, MutableList<Any?>::toList)

fun toSet(elementType: KType, data: UByteArray, offset: Int): Pair<Int, Set<Any?>> =
    toArrayContainer(elementType, data, offset, MutableList<Any?>::toSet)

fun toCollection(elementType: KType, data: UByteArray, offset: Int): Pair<Int, Collection<Any?>> =
    toArrayContainer(elementType, data, offset, MutableList<Any?>::toList)

fun toMap(type: KType, data: UByteArray, offset: Int): Pair<Int, Map<Any?, Any?>> {
    return toObjectContainer(
        keyType(type), { it }, { valueType(type) },
        data, offset, MutableMap<Any?, Any?>::toMap
    )
}

fun toData(type: KType, data: UByteArray, offset: Int): Pair<Int, Any> {
    val dataClass = type.classifier as KClass<*>
    val constructor = dataClass.primaryConstructor!!
    return toObjectContainer(
        typeOf<String>(), constructor.parameters::get, KParameter::type,
        data, offset, constructor::callBy
    )
}

private operator fun List<KParameter>.get(key: Any?) =
    this.firstOrNull { it.name == key }
        ?: throw IllegalArgumentException("Unknown key found: $key")

fun <T> toArrayContainer(
    type: KType,
    data: UByteArray,
    offset: Int,
    containerMapper: (MutableList<Any?>) -> T
): Pair<Int, T> {
    if (data[offset] != ARRAY_START) throw IllegalArgumentException("Invalid start of element container")
    val list = mutableListOf<Any?>()
    var i = offset + 1
    while (true) {
        val decodeElement = decode(elemType(type), data, i)
        i = decodeElement.first
        val element = decodeElement.second

        list.add(element)
        if (data[i] == CONTAINER_END) break
        if (data[i] != ELEMENT_SEPARATOR) throw IllegalArgumentException("Unexpected character found. Expected to find an element separator")
        i++
    }
    return i + 1 to containerMapper.invoke(list)
}

fun <T, U> toObjectContainer(
    keyType: KType,
    keyMapper: (Any?) -> U,
    valueTypeResolver: (U) -> KType,
    data: UByteArray,
    offset: Int,
    containerMapper: (MutableMap<U, Any?>) -> T
): Pair<Int, T> {
    if (data[offset] != OBJECT_START) throw IllegalArgumentException("Invalid start of object container")
    val map = mutableMapOf<U, Any?>()
    var i = offset + 1
    while (true) {
        val decodeKey = decode(keyType, data, i)
        i = decodeKey.first
        val key = keyMapper(decodeKey.second)

        if (data[i] != ELEMENT_SEPARATOR) throw IllegalArgumentException("Unexpected character found. Expected to find an element separator")
        i++

        val valueType = valueTypeResolver(key)
        val decodeValue = decode(valueType, data, i)
        i = decodeValue.first
        val value = decodeValue.second

        map[key] = value
        if (data[i] == CONTAINER_END) break
        if (data[i] != ELEMENT_SEPARATOR) throw IllegalArgumentException("Unexpected character found. Expected to find an element separator")
        i++
    }
    return i + 1 to containerMapper.invoke(map)
}
