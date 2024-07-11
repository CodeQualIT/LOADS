@file:OptIn(ExperimentalUnsignedTypes::class, ExperimentalStdlibApi::class)

package nl.cqit.loads

import nl.cqit.loads.model.BINARY_VALUE
import nl.cqit.loads.model.BinaryType
import nl.cqit.loads.model.BinaryType.Companion.PREDEFINED_BINARY_TYPE_CATEGORIES
import nl.cqit.loads.model.INVALID_BINARY_VALUE_MSG
import nl.cqit.loads.model.INVALID_STRING_CHARACTER_MSG
import nl.cqit.loads.model.SPECIAL_BYTES
import nl.cqit.loads.model.CUSTOM_BINARY_TYPE_END
import nl.cqit.loads.model.CUSTOM_BINARY_TYPE_START
import nl.cqit.loads.model.VALUE_TERMINATORS
import nl.cqit.loads.utils.andThen
import nl.cqit.loads.utils.cast
import nl.cqit.loads.utils.decodeBase64
import nl.cqit.loads.utils.elemType
import nl.cqit.loads.utils.get
import nl.cqit.loads.utils.isData
import nl.cqit.loads.utils.keyType
import nl.cqit.loads.utils.toArrayContainer
import nl.cqit.loads.utils.toObjectContainer
import nl.cqit.loads.utils.valueType
import kotlin.reflect.*
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.primaryConstructor

inline fun <reified T> decode(data: UByteArray): T {
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
        isData(type.classifier) -> toData(type, data, offset)
        type.classifier == String::class -> toString(type, data, offset)
        type.classifier == UByteArray::class -> toUByteArray(type, data, offset)
        type.classifier == ByteArray::class -> toByteArray(type, data, offset)
//        ByteArray::class -> TODO()
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

private fun toString(type: KType, data: UByteArray, offset: Int): Pair<Int, String> {
    val (newOffset, value) = extractNextValue(data, offset, VALUE_TERMINATORS)
    return newOffset to String(value.toByteArray())
}

private fun toUByteArray(type: KType, data: UByteArray, offset: Int): Pair<Int, UByteArray> {
    val (newOffset, value, _) = extractBinary(type, data, offset)
    return newOffset to value
}

private fun toByteArray(type: KType, data: UByteArray, offset: Int): Pair<Int, ByteArray> {
    val (newOffset, value, _) = extractBinary(type, data, offset)
    return newOffset to value.toByteArray()
}

private fun toArray(containerType: KType, data: UByteArray, offset: Int): Pair<Int, Array<*>> =
    toArrayContainer(containerType, MutableList<*>::toTypedArray.andThen { cast(containerType.elemType()) }, data, offset)

private fun toList(containerType: KType, data: UByteArray, offset: Int): Pair<Int, List<*>> =
    toArrayContainer(containerType, MutableList<*>::toList, data, offset)

private fun toSet(containerType: KType, data: UByteArray, offset: Int): Pair<Int, Set<*>> =
    toArrayContainer(containerType, MutableList<*>::toSet, data, offset)

private fun toCollection(containerType: KType, data: UByteArray, offset: Int): Pair<Int, Collection<*>> =
    toArrayContainer(containerType, MutableList<*>::toList, data, offset)

private fun toMap(containerType: KType, data: UByteArray, offset: Int): Pair<Int, Map<*, *>> {
    return toObjectContainer(
        containerType.keyType(), { it }, { containerType.valueType() },
        MutableMap<*, *>::toMap, data, offset
    )
}

private fun toData(type: KType, data: UByteArray, offset: Int): Pair<Int, *> {
    val dataClass = type.classifier as KClass<*>
    val constructor = dataClass.primaryConstructor!!
    return toObjectContainer(
        typeOf<String>(), constructor.parameters::get, KParameter::type,
        constructor::callBy, data, offset
    )
}

private fun extractBinary(type: KType, data: UByteArray, offset: Int): Triple<Int, UByteArray, BinaryType?> {
    require(data[offset] == BINARY_VALUE) { INVALID_BINARY_VALUE_MSG + offset }
    val (valueOffset, binaryType) = when {
        data[offset + 1] in PREDEFINED_BINARY_TYPE_CATEGORIES -> offset + 3 to data.sliceArray(offset + 1 until offset + 3)
        data[offset + 1] == CUSTOM_BINARY_TYPE_START -> {
            extractNextValue(data, offset + 2, ubyteArrayOf(CUSTOM_BINARY_TYPE_END))
                .let { (newOffset, value) -> newOffset + 1 to value }
        }
        else -> offset + 1 to null
    }
    val (newOffset, value) = extractNextValue(data, valueOffset, VALUE_TERMINATORS)
    return Triple(newOffset, value.decodeBase64(), binaryType?.let(BinaryType::valueOf))
}

private fun extractNextValue(data: UByteArray, offset: Int, terminators: UByteArray): Pair<Int, UByteArray> {
    var i = offset
    while (i < data.size && data[i] !in terminators) i++
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
    return i to slice
}


