@file:OptIn(ExperimentalUnsignedTypes::class, ExperimentalStdlibApi::class)

package nl.cqit.loads

import nl.cqit.loads.utils.andThen
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import kotlin.reflect.*
import kotlin.reflect.full.cast
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.primaryConstructor

private const val INVALID_STRING_CHARACTER_MSG = "Invalid character(s) found in string value: "
private const val INVALID_ARRAY_START_MSG = "Invalid start of array container at position "
private const val INVALID_OBJECT_START_MSG = "Invalid start of object container at position "
private const val EXPECTED_ELEMENT_SEPARATOR_MSG =
    "Unexpected character found. Expected to find an element separator at position "

private fun elemType(type: KType) = type.arguments[0].type!!
private fun keyType(type: KType) = type.arguments[0].type!!
private fun valueType(type: KType) = type.arguments[1].type!!

//fun main() {
//    val data = ubyteArrayOf(
//        0xFCu,
//        *"map".toUByteArray(UTF_8),
//        0xFFu,
//        0xFCu,
//        0x34u, 0x35u, 0x36u,
//        0xFFu,
//        0x37u, 0x38u, 0x39u,
//        0xFEu,
//        0xFFu,
//        *"arr".toUByteArray(UTF_8),
//        0xFFu,
//        0xFAu,
//        0x31u, 0x32u, 0x33u,
//        0xFFu,
//        0x34u, 0x35u, 0x36u,
//        0xFEu,
//        0xFEu
//    )
//    val result: Obj = decode(data)
//    println(result)
//}

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

private fun Array<*>.castArray(subType: KType): Array<*> = when {
    subType.isSubtypeOf(typeOf<Array<*>>()) -> map { it as Array<*> }
        .map { castArray(elemType(subType)) }
        .toTypedArray()

    subType.classifier == List::class -> map { it as List<*> }.toTypedArray()
    subType.classifier == Set::class -> map { it as Set<*> }.toTypedArray()
    subType.classifier == Collection::class -> map { it as Collection<*> }.toTypedArray()
    subType.classifier == Map::class -> map { it as Map<*, *> }.toTypedArray()
    subType.classifier == String::class -> map { it as String }.toTypedArray()
    isData(subType.classifier) -> map((subType.classifier!! as KClass<*>)::cast).toTypedArray()
    subType.classifier == ByteArray::class -> map { it as ByteArray }.toTypedArray()
    subType.classifier == UByteArray::class -> map { it as UByteArray }.toTypedArray()
    subType.classifier == String::class -> map { it as String }.toTypedArray()
    subType.classifier == Byte::class -> map { it as Byte }.toTypedArray()
    subType.classifier == Short::class -> map { it as Short }.toTypedArray()
    subType.classifier == Int::class -> map { it as Int }.toTypedArray()
    subType.classifier == Long::class -> map { it as Long }.toTypedArray()
    subType.classifier == UByte::class -> map { it as UByte }.toTypedArray()
    subType.classifier == UShort::class -> map { it as UShort }.toTypedArray()
    subType.classifier == UInt::class -> map { it as UInt }.toTypedArray()
    subType.classifier == ULong::class -> map { it as ULong }.toTypedArray()
    subType.classifier == Float::class -> map { it as Float }.toTypedArray()
    subType.classifier == Double::class -> map { it as Double }.toTypedArray()
    subType.classifier == Boolean::class -> map { it as Boolean }.toTypedArray()
    subType.classifier == Instant::class -> map { it as Instant }.toTypedArray()
    subType.classifier == OffsetDateTime::class -> map { it as OffsetDateTime }.toTypedArray()
    subType.classifier == ZonedDateTime::class -> map { it as ZonedDateTime }.toTypedArray()
    else -> throw NotImplementedError()
}

private fun isData(classifier: KClassifier?): Boolean =
    classifier is KClass<*> && classifier.isData


private fun toString(data: UByteArray, offset: Int): Pair<Int, String> {
    var i = offset
    while (i < data.size && data[i] !in VALUE_TERMINATORS) i++
    val slice = data.sliceArray(offset until i)
    val specialCharactersInString = slice.intersect(SPECIAL_BYTES)
    require(specialCharactersInString.isEmpty()) {
        val invalidCharacters = specialCharactersInString.map {
            val hex = it.toHexString(HexFormat.UpperCase)
            val pos = slice.indexOf(it) + offset
            "Ox$hex at position $pos"
        }
        INVALID_STRING_CHARACTER_MSG + invalidCharacters.joinToString()
    }
    return i to String(slice.toByteArray())
}

private fun toArray(containerType: KType, data: UByteArray, offset: Int): Pair<Int, Array<*>> =
    toArrayContainer(containerType, data, offset, MutableList<*>::toTypedArray
        .andThen { castArray(elemType(containerType)) })

private fun toList(containerType: KType, data: UByteArray, offset: Int): Pair<Int, List<*>> =
    toArrayContainer(containerType, data, offset, MutableList<*>::toList)

private fun toSet(containerType: KType, data: UByteArray, offset: Int): Pair<Int, Set<*>> =
    toArrayContainer(containerType, data, offset, MutableList<*>::toSet)

private fun toCollection(
    containerType: KType,
    data: UByteArray,
    offset: Int
): Pair<Int, Collection<*>> =
    toArrayContainer(containerType, data, offset, MutableList<*>::toList)

private fun toMap(containerType: KType, data: UByteArray, offset: Int): Pair<Int, Map<*, *>> {
    return toObjectContainer(
        keyType(containerType), { it }, { valueType(containerType) },
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
    firstOrNull { it.name == key }
        ?: throw IllegalArgumentException("Unknown key found: $key")


private fun <T> toArrayContainer(
    type: KType,
    data: UByteArray,
    offset: Int,
    containerMapper: (MutableList<*>) -> T
): Pair<Int, T> {
    require(data[offset] == ARRAY_START) { INVALID_ARRAY_START_MSG + offset }
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
