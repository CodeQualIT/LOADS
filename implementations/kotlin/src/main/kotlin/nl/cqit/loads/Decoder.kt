@file:OptIn(ExperimentalUnsignedTypes::class)

package nl.cqit.loads

import nl.cqit.loads.model.VALUE_TERMINATORS
import nl.cqit.loads.model.types.ShortType.*
import nl.cqit.loads.model.types.SingleBooleanType
import nl.cqit.loads.model.types.TimestampType
import nl.cqit.loads.utils.*
import java.nio.ByteBuffer
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
        type.classifier == String::class -> toString(data, offset)
        type.classifier == UByteArray::class -> toUByteArray(data, offset)
        type.classifier == ByteArray::class -> toByteArray(data, offset)
        type.classifier == Byte::class -> toByte(data, offset)
        type.classifier == Short::class -> toShort(data, offset)
        type.classifier == Int::class -> toInt(data, offset)
        type.classifier == Long::class -> toLong(data, offset)
        type.classifier == UByte::class -> toUByte(data, offset)
        type.classifier == UShort::class -> toUShort(data, offset)
        type.classifier == UInt::class -> toUInt(data, offset)
        type.classifier == ULong::class -> toULong(data, offset)
        type.classifier == Float::class -> toFloat(data, offset)
        type.classifier == Double::class -> toDouble(data, offset)
        type.classifier == Boolean::class -> toBoolean(data, offset)
        type.classifier == Instant::class -> toInstant(type, data, offset)
        type.classifier == OffsetDateTime::class -> toOffsetDateTime(type, data, offset)
        type.classifier == ZonedDateTime::class -> toZonedDateTime(type, data, offset)
        else -> throw NotImplementedError()
    }
    if (result == null && !type.isMarkedNullable) error("Expected non-null type $type value but got null.")
    return newOffset to result
}

private fun toString(data: UByteArray, offset: Int): Pair<Int, String?> {
    val (newOffset, value) = extractNextValue(data, offset, VALUE_TERMINATORS)
    return newOffset to value?.toByteArray()?.let { String(it) }
}

private fun toUByteArray(data: UByteArray, offset: Int): Pair<Int, UByteArray?> {
    val (newOffset, value, _) = extractBinary(data, offset)
    return newOffset to value
}

private fun toByteArray(data: UByteArray, offset: Int): Pair<Int, ByteArray?> {
    val (newOffset, value, _) = extractBinary(data, offset)
    return newOffset to value?.toByteArray()
}

private fun toByte(data: UByteArray, offset: Int): Pair<Int, Byte?> {
    val (newOffset, value, binaryType) = extractBinary(data, offset)
    require(binaryType == null || binaryType == BYTE) { "Expected Byte but got $binaryType" }
    return newOffset to value?.toByte()
}

private fun toShort(data: UByteArray, offset: Int): Pair<Int, Short?> {
    val (newOffset, value, binaryType) = extractBinary(data, offset)
    require(binaryType == null || binaryType == SHORT) { "Expected Short but got $binaryType" }
    return newOffset to value?.toShort()
}

private fun toInt(data: UByteArray, offset: Int): Pair<Int, Int?> {
    val (newOffset, value, binaryType) = extractBinary(data, offset)
    require(binaryType == null || binaryType == INT) { "Expected Int but got $binaryType" }
    return newOffset to value?.toInt()
}

private fun toLong(data: UByteArray, offset: Int): Pair<Int, Long?> {
    val (newOffset, value, binaryType) = extractBinary(data, offset)
    require(binaryType == null || binaryType == LONG) { "Expected Long but got $binaryType" }
    return newOffset to value?.toLong()
}

private fun toUByte(data: UByteArray, offset: Int): Pair<Int, UByte?> {
    val (newOffset, value, binaryType) = extractBinary(data, offset)
    require(binaryType == null || binaryType == UBYTE) { "Expected UByte but got $binaryType" }
    return newOffset to value?.toUByte()
}

private fun toUShort(data: UByteArray, offset: Int): Pair<Int, UShort?> {
    val (newOffset, value, binaryType) = extractBinary(data, offset)
    require(binaryType == null || binaryType == USHORT) { "Expected UShort but got $binaryType" }
    return newOffset to value?.toUShort()
}

private fun toUInt(data: UByteArray, offset: Int): Pair<Int, UInt?> {
    val (newOffset, value, binaryType) = extractBinary(data, offset)
    require(binaryType == null || binaryType == UINT) { "Expected UInt but got $binaryType" }
    return newOffset to value?.toUInt()
}

private fun toULong(data: UByteArray, offset: Int): Pair<Int, ULong?> {
    val (newOffset, value, binaryType) = extractBinary(data, offset)
    require(binaryType == null || binaryType == ULONG) { "Expected ULong but got $binaryType" }
    return newOffset to value?.toULong()
}

private fun toFloat(data: UByteArray, offset: Int): Pair<Int, Float?> {
    val (newOffset, value, binaryType) = extractBinary(data, offset)
    require(binaryType == null || binaryType == FLOAT) { "Expected Float but got $binaryType" }
    return newOffset to value?.toByteBuffer()?.getFloat()
}

private fun toDouble(data: UByteArray, offset: Int): Pair<Int, Double?> {
    val (newOffset, value, binaryType) = extractBinary(data, offset)
    require(binaryType == null || binaryType == DOUBLE) { "Expected Double but got $binaryType" }
    return newOffset to value?.toByteBuffer()?.getDouble()
}

private fun toBoolean(data: UByteArray, offset: Int): Pair<Int, Boolean?> {
    val (newOffset, value, binaryType) = extractBinary(data, offset)
    require(binaryType is SingleBooleanType?) { "Expected Boolean but got $binaryType" }
    return newOffset to value?.let {
        when (binaryType) {
            null, BOOLEAN1 -> value[0] !in listOf<UByte>(
                0u,
                5u,  // "F" in base64
                31u, // "f" in base64
                52u, // "0" in base64
            )
            TRUE -> true
            FALSE -> false
        }
    }
}

private fun toInstant(type: KType, data: UByteArray, offset: Int): Pair<Int, Instant?> {
    val (newOffset, value, binaryType) = extractBinary(data, offset)
    require(binaryType is TimestampType?) { "Expected Instant but got $binaryType" }
    val valueByteBuffer = value?.toByteBuffer()
    return newOffset to valueByteBuffer?.toInstant(binaryType, value)
}

private fun toOffsetDateTime(type: KType, data: UByteArray, offset: Int): Pair<Int, OffsetDateTime?> {
    val (newOffset, value, binaryType) = extractBinary(data, offset)
    require(binaryType is TimestampType) { "Expected OffsetDateTime but got $binaryType" }
    val valueByteBuffer = value?.toByteBuffer()
    // TODO add support for other time zone offsets (spec doesn't support passing along the time zone offset yet)
    return newOffset to valueByteBuffer?.toInstant(binaryType, value)?.atOffset(UTC)
}

private fun toZonedDateTime(type: KType, data: UByteArray, offset: Int): Pair<Int, ZonedDateTime?> {
    val (newOffset, value, binaryType) = extractBinary(data, offset)
    require(binaryType is TimestampType) { "Expected ZonedDateTime but got $binaryType" }
    val valueByteBuffer = value?.toByteBuffer()
    // TODO add support for other time zones (spec doesn't support passing along the time zone yet)
    return newOffset to valueByteBuffer?.toInstant(binaryType, value)?.atZone(UTC)
}

private fun ByteBuffer.toInstant(timestampType: TimestampType?, value: UByteArray): Instant? =
    if (timestampType != null)
        toInstantFromType(timestampType, this)
    else
        toInstantFromBufferSize(value.size, this)

private fun toInstantFromType(type: TimestampType, valueByteBuffer: ByteBuffer): Instant? = when (type) {
    TIMESTAMP4 -> Instant.ofEpochSecond(valueByteBuffer.getInt().toLong())
    TIMESTAMP8 -> Instant.ofEpochMilli(valueByteBuffer.getLong())
    TIMESTAMP12 -> Instant.ofEpochSecond(valueByteBuffer.getInt().toLong(), valueByteBuffer.getInt().toLong())
}

private fun toInstantFromBufferSize(size: Int, valueByteBuffer: ByteBuffer): Instant? = when (size) {
    4 -> Instant.ofEpochSecond(valueByteBuffer.getInt().toLong())
    8 -> Instant.ofEpochMilli(valueByteBuffer.getLong())
    12 -> Instant.ofEpochSecond(valueByteBuffer.getInt().toLong(), valueByteBuffer.getInt().toLong())
    else -> error("Invalid Instant size: $size. Required 4, 8 or 12 bytes.")
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


