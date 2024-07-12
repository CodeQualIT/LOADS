@file:OptIn(ExperimentalUnsignedTypes::class, ExperimentalStdlibApi::class)

package nl.cqit.loads

import nl.cqit.loads.model.*
import nl.cqit.loads.model.BINARY_VALUE
import nl.cqit.loads.model.BinaryType
import nl.cqit.loads.model.BinaryType.Companion.PREDEFINED_BINARY_TYPE_CATEGORIES
import nl.cqit.loads.model.CUSTOM_BINARY_TYPE_END
import nl.cqit.loads.model.CUSTOM_BINARY_TYPE_START
import nl.cqit.loads.model.INVALID_BINARY_VALUE_MSG
import nl.cqit.loads.model.INVALID_STRING_CHARACTER_MSG
import nl.cqit.loads.model.SPECIAL_BYTES
import nl.cqit.loads.model.ShortType.*
import nl.cqit.loads.model.ShortType.BYTE
import nl.cqit.loads.model.ShortType.INT
import nl.cqit.loads.model.ShortType.SHORT
import nl.cqit.loads.model.VALUE_TERMINATORS
import nl.cqit.loads.utils.*
import nl.cqit.loads.utils.cast
import nl.cqit.loads.utils.decodeBase64
import nl.cqit.loads.utils.elemType
import nl.cqit.loads.utils.get
import nl.cqit.loads.utils.isData
import nl.cqit.loads.utils.keyType
import nl.cqit.loads.utils.toArrayContainer
import nl.cqit.loads.utils.toObjectContainer
import nl.cqit.loads.utils.valueType
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset.UTC
import java.time.ZonedDateTime
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
        type.classifier == Byte::class -> toByte(type, data, offset)
        type.classifier == Short::class -> toShort(type, data, offset)
        type.classifier == Int::class -> toInt(type, data, offset)
        type.classifier == Long::class -> toLong(type, data, offset)
        type.classifier == UByte::class -> toUByte(type, data, offset)
        type.classifier == UShort::class -> toUShort(type, data, offset)
        type.classifier == UInt::class -> toUInt(type, data, offset)
        type.classifier == ULong::class -> toULong(type, data, offset)
        type.classifier == Float::class -> toFloat(type, data, offset)
        type.classifier == Double::class -> toDouble(type, data, offset)
        type.classifier == Boolean::class -> toBoolean(type, data, offset)
        type.classifier == Instant::class -> toInstant(type, data, offset)
        type.classifier == OffsetDateTime::class -> toOffsetDateTime(type, data, offset)
        type.classifier == ZonedDateTime::class -> toZonedDateTime(type, data, offset)
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

private fun toByte(type: KType, data: UByteArray, offset: Int): Pair<Int, Byte> {
    val (newOffset, value, binaryType) = extractBinary(type, data, offset)
    require(binaryType == BYTE) { "Expected Byte but got $binaryType" }
    return newOffset to value.toByteBuffer().get()
}

private fun toShort(type: KType, data: UByteArray, offset: Int): Pair<Int, Short> {
    val (newOffset, value, binaryType) = extractBinary(type, data, offset)
    require(binaryType == SHORT) { "Expected Short but got $binaryType" }
    return newOffset to value.toByteBuffer().getShort()
}

private fun toInt(type: KType, data: UByteArray, offset: Int): Pair<Int, Int> {
    val (newOffset, value, binaryType) = extractBinary(type, data, offset)
    require(binaryType == INT) { "Expected Int but got $binaryType" }
    return newOffset to value.toByteBuffer().getInt()
}

private fun toLong(type: KType, data: UByteArray, offset: Int): Pair<Int, Long> {
    val (newOffset, value, binaryType) = extractBinary(type, data, offset)
    require(binaryType == LONG) { "Expected Long but got $binaryType" }
    return newOffset to value.toByteBuffer().getLong()
}

private fun toUByte(type: KType, data: UByteArray, offset: Int): Pair<Int, UByte> {
    val (newOffset, value, binaryType) = extractBinary(type, data, offset)
    require(binaryType == UBYTE) { "Expected UByte but got $binaryType" }
    return newOffset to value.toByteBuffer().get().toUByte()
}

private fun toUShort(type: KType, data: UByteArray, offset: Int): Pair<Int, UShort> {
    val (newOffset, value, binaryType) = extractBinary(type, data, offset)
    require(binaryType == USHORT) { "Expected UShort but got $binaryType" }
    return newOffset to value.toByteBuffer().getShort().toUShort()
}

private fun toUInt(type: KType, data: UByteArray, offset: Int): Pair<Int, UInt> {
    val (newOffset, value, binaryType) = extractBinary(type, data, offset)
    require(binaryType == UINT) { "Expected UInt but got $binaryType" }
    return newOffset to value.toByteBuffer().getInt().toUInt()
}

private fun toULong(type: KType, data: UByteArray, offset: Int): Pair<Int, ULong> {
    val (newOffset, value, binaryType) = extractBinary(type, data, offset)
    require(binaryType == ULONG) { "Expected ULong but got $binaryType" }
    return newOffset to value.toByteBuffer().getLong().toULong()
}

private fun toFloat(type: KType, data: UByteArray, offset: Int): Pair<Int, Float> {
    val (newOffset, value, binaryType) = extractBinary(type, data, offset)
    require(binaryType == FLOAT) { "Expected Float but got $binaryType" }
    return newOffset to value.toByteBuffer().getFloat()
}

private fun toDouble(type: KType, data: UByteArray, offset: Int): Pair<Int, Double> {
    val (newOffset, value, binaryType) = extractBinary(type, data, offset)
    require(binaryType == DOUBLE) { "Expected Double but got $binaryType" }
    return newOffset to value.toByteBuffer().getDouble()
}

private fun toBoolean(type: KType, data: UByteArray, offset: Int): Pair<Int, Boolean> {
    val (newOffset, value , binaryType) = extractBinary(type, data, offset)
    require(binaryType is SingleBooleanType) { "Expected Boolean but got $binaryType" }
    return newOffset to if (binaryType == BOOLEAN1) value[0] == 1.toUByte() else binaryType == TRUE
}

private fun toInstant(type: KType, data: UByteArray, offset: Int): Pair<Int, Instant> {
    val (newOffset, value, binaryType) = extractBinary(type, data, offset)
    require(binaryType is TimestampType) { "Expected Instant but got $binaryType" }
    return newOffset to when (binaryType) {
        is TIMESTAMP4 -> Instant.ofEpochSecond(value.toByteBuffer().getInt().toLong())
        is TIMESTAMP8 -> Instant.ofEpochMilli(value.toByteBuffer().getLong())
        is TIMESTAMP12 -> Instant.ofEpochSecond(value.toByteBuffer().getInt().toLong(), value.toByteBuffer().getInt().toLong())
    }
}

private fun toOffsetDateTime(type: KType, data: UByteArray, offset: Int): Pair<Int, OffsetDateTime> {
    val (newOffset, value, binaryType) = extractBinary(type, data, offset)
    require(binaryType is TimestampType) { "Expected OffsetDateTime but got $binaryType" }
    // TODO add support for other time zone offsets (spec doesn't support passing along the time zone offset yet)
    return newOffset to when (binaryType) {
        is TIMESTAMP4 -> Instant.ofEpochSecond(value.toByteBuffer().getInt().toLong()).atOffset(UTC)
        is TIMESTAMP8 -> Instant.ofEpochMilli(value.toByteBuffer().getLong()).atOffset(UTC)
        is TIMESTAMP12 -> Instant.ofEpochSecond(value.toByteBuffer().getInt().toLong(), value.toByteBuffer().getInt().toLong()).atOffset(UTC)
    }
}

private fun toZonedDateTime(type: KType, data: UByteArray, offset: Int): Pair<Int, ZonedDateTime> {
    val (newOffset, value, binaryType) = extractBinary(type, data, offset)
    require(binaryType is TimestampType) { "Expected ZonedDateTime but got $binaryType" }
    // TODO add support for other time zones (spec doesn't support passing along the time zone yet)
    return newOffset to when (binaryType) {
        is TIMESTAMP4 -> Instant.ofEpochSecond(value.toByteBuffer().getInt().toLong()).atZone(UTC)
        is TIMESTAMP8 -> Instant.ofEpochMilli(value.toByteBuffer().getLong()).atZone(UTC)
        is TIMESTAMP12 -> Instant.ofEpochSecond(value.toByteBuffer().getInt().toLong(), value.toByteBuffer().getInt().toLong()).atZone(UTC)
    }
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


