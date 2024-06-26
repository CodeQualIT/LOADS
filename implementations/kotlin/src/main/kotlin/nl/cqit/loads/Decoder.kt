@file:OptIn(ExperimentalUnsignedTypes::class, ExperimentalStdlibApi::class)

package nl.cqit.loads

import nl.cqit.loads.utils.toUByteArray
import kotlin.reflect.*
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.primaryConstructor
import kotlin.text.Charsets.UTF_8

private const val INVALID_STRING_CHARACTER_MSG = "Invalid character(s) found in string value: "
private const val INVALID_ARRAY_START_MSG = "Invalid start of array container at position "
private const val INVALID_OBJECT_START_MSG = "Invalid start of object container at position "
private const val EXPECTED_ELEMENT_SEPARATOR_MSG =
    "Unexpected character found. Expected to find an element separator at position "

private const val ARRAY_START: UByte = 0xFAu
private const val BINARY_VALUE: UByte = 0xFBu
private const val OBJECT_START: UByte = 0xFCu
private const val NULL_VALUE: UByte = 0xFDu
private const val CONTAINER_END: UByte = 0xFEu
private const val ELEMENT_SEPARATOR: UByte = 0xFFu

private val SPECIAL_BYTES = ubyteArrayOf(
    ARRAY_START,
    BINARY_VALUE,
    OBJECT_START,
    NULL_VALUE,
    CONTAINER_END,
    ELEMENT_SEPARATOR
)
private val VALUE_TERMINATORS = ubyteArrayOf(
    ELEMENT_SEPARATOR,
    CONTAINER_END
)

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
    val result: Obj = decode(data)
    println(result)
}

inline fun <reified T : Any> decode(data: UByteArray): T {
    val type = typeOf<T>()
    return decode(type, data, 0).second as T
}

fun decode(type: KType, data: UByteArray, offset: Int): Pair<Int, *> {
    val (newOffset: Int, result: Any?) = when {
        type.isSubtypeOf(typeOf<Array<*>>()) -> toArray(type, data, offset)
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

private fun isData(classifier: KClassifier?): Boolean =
    classifier is KClass<*> && classifier.isData


private fun toString(data: UByteArray, offset: Int): Pair<Int, String> {
    var i = offset
    while (i < data.size && data[i] !in VALUE_TERMINATORS) i++
    val slice = data.sliceArray(offset until i)
    val specialCharactersInString = slice.intersect(SPECIAL_BYTES)
    require (specialCharactersInString.isEmpty()) {
        val invalidCharacters = specialCharactersInString.map {
            val hex = it.toHexString(HexFormat.UpperCase)
            val pos = slice.indexOf(it) + offset
            "Ox$hex at position $pos"
        }
        INVALID_STRING_CHARACTER_MSG + invalidCharacters.joinToString()
    }
    return i to String(slice.toByteArray())
}

private fun toArray(elementType: KType, data: UByteArray, offset: Int): Pair<Int, Array<*>> =
    toArrayContainer(elementType, data, offset, MutableList<*>::toTypedArray)

private fun toList(elementType: KType, data: UByteArray, offset: Int): Pair<Int, List<*>> =
    toArrayContainer(elementType, data, offset, MutableList<*>::toList)

private fun toSet(elementType: KType, data: UByteArray, offset: Int): Pair<Int, Set<*>> =
    toArrayContainer(elementType, data, offset, MutableList<*>::toSet)

private fun toCollection(
    elementType: KType,
    data: UByteArray,
    offset: Int
): Pair<Int, Collection<*>> =
    toArrayContainer(elementType, data, offset, MutableList<*>::toList)

private fun toMap(type: KType, data: UByteArray, offset: Int): Pair<Int, Map<*, *>> {
    return toObjectContainer(
        keyType(type), { it }, { valueType(type) },
        data, offset, MutableMap<*, *>::toMap
    )
}

private fun toData(type: KType, data: UByteArray, offset: Int): Pair<Int, *> {
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


private fun <T> toArrayContainer(
    type: KType,
    data: UByteArray,
    offset: Int,
    containerMapper: (MutableList<*>) -> T
): Pair<Int, T> {
    require (data[offset] == ARRAY_START) { INVALID_ARRAY_START_MSG + offset }
    val list = mutableListOf<Any?>()
    var i = offset + 1
    while (true) {
        val decodeElement = decode(elemType(type), data, i)
        i = decodeElement.first
        val element = decodeElement.second

        list.add(element)
        if (data[i] == CONTAINER_END) break
        require(data[i] == ELEMENT_SEPARATOR) { EXPECTED_ELEMENT_SEPARATOR_MSG + i }
        i++
    }
    return i + 1 to containerMapper(list)
}


private fun <T, U> toObjectContainer(
    keyType: KType,
    keyMapper: (Any?) -> U,
    valueTypeResolver: (U) -> KType,
    data: UByteArray,
    offset: Int,
    containerMapper: (MutableMap<U, *>) -> T
): Pair<Int, T> {
    require(data[offset] == OBJECT_START) { INVALID_OBJECT_START_MSG + offset }
    val map = mutableMapOf<U, Any?>()
    var i = offset + 1
    while (true) {
        val decodeKey = decode(keyType, data, i)
        i = decodeKey.first
        val key = keyMapper(decodeKey.second)

        require(data[i] == ELEMENT_SEPARATOR) { EXPECTED_ELEMENT_SEPARATOR_MSG + i }
        i++

        val valueType = valueTypeResolver(key)
        val decodeValue = decode(valueType, data, i)
        i = decodeValue.first
        val value = decodeValue.second

        map[key] = value
        if (data[i] == CONTAINER_END) break
        require(data[i] == ELEMENT_SEPARATOR) { EXPECTED_ELEMENT_SEPARATOR_MSG + i }
        i++
    }
    return i + 1 to containerMapper.invoke(map)
}
