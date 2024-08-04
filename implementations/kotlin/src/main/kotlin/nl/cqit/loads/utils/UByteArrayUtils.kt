@file:OptIn(ExperimentalUnsignedTypes::class)

package nl.cqit.loads.utils

import nl.cqit.loads.model.types.ShortType.*
import nl.cqit.loads.model.types.ShortType.TIMESTAMP12
import nl.cqit.loads.model.types.ShortType.TIMESTAMP4
import nl.cqit.loads.model.types.ShortType.TIMESTAMP8
import nl.cqit.loads.model.types.SingleBooleanType
import nl.cqit.loads.model.types.TimestampType
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.time.Instant

internal fun UByteArray.toByteBuffer(): ByteBuffer =
    ByteBuffer.wrap(toByteArray())

internal fun UByteArray.toByte(): Byte = toByteBuffer().get()

internal fun UByteArray.toShort(): Short = when (size) {
    Byte.SIZE_BYTES -> toByteBuffer().get().toShort()
    else -> toByteBuffer().short
}

internal fun UByteArray.toInt(): Int {
    val byteBuffer = toByteBuffer()
    return when (size) {
        Byte.SIZE_BYTES -> byteBuffer.get().toInt()
        Short.SIZE_BYTES -> byteBuffer.short.toInt()
        3 -> (0u
                or (byteBuffer.get().toUByte().toUInt() shl 16)
                or (byteBuffer.short.toUShort().toUInt())
                ).toInt()
        else -> byteBuffer.int
    }
}

internal fun UByteArray.toLong(suffixLength: Int = 0, skipBytes: Int = 0): Long {
    val byteBuffer = toByteBuffer().repeatApply(skipBytes, ByteBuffer::get)
    val valueSize = size - skipBytes - suffixLength
    return when (valueSize) {
        Byte.SIZE_BYTES -> byteBuffer.get().toLong()
        Short.SIZE_BYTES -> byteBuffer.short.toLong()
        3 -> (0uL
                or (byteBuffer.get().toUByte().toULong() shl 16)
                or (byteBuffer.short.toUShort().toULong())
                ).toLong()
        Int.SIZE_BYTES -> byteBuffer.int.toLong()
        5 -> (0uL
                or (byteBuffer.get().toUByte().toULong() shl 32)
                or (byteBuffer.int.toUInt().toULong())
                ).toLong()
        6 -> (0uL
                or (byteBuffer.short.toUShort().toULong() shl 32)
                or (byteBuffer.int.toUInt().toULong())
                ).toLong()
        7 -> (0uL
                or (byteBuffer.get().toUByte().toULong() shl 48)
                or (byteBuffer.short.toUShort().toULong() shl 32)
                or (byteBuffer.int.toUInt().toULong())
                ).toLong()
        else -> byteBuffer.long
    }
}

internal fun UByteArray.toUByte(): UByte = toByteBuffer().get().toUByte()

internal fun UByteArray.toUShort(): UShort = when (size) {
    Byte.SIZE_BYTES -> toByteBuffer().get().toUShort()
    else -> toByteBuffer().short.toUShort()
}

internal fun UByteArray.toUInt(): UInt {
    val byteBuffer = toByteBuffer()
    return when (size) {
        Byte.SIZE_BYTES -> byteBuffer.get().toUInt()
        Short.SIZE_BYTES -> byteBuffer.short.toUInt()
        3 -> (0u
                or (byteBuffer.get().toUByte().toUInt() shl 16)
                or (byteBuffer.short.toUShort().toUInt()))
        else -> byteBuffer.int.toUInt()
    }
}

internal fun UByteArray.toULong(): ULong {
    val byteBuffer = toByteBuffer()
    return when (size) {
        Byte.SIZE_BYTES -> byteBuffer.get().toULong()
        Short.SIZE_BYTES -> byteBuffer.short.toULong()
        3 -> (0uL
                or (byteBuffer.get().toUByte().toULong() shl 16)
                or (byteBuffer.short.toUShort().toULong()))
        Int.SIZE_BYTES -> byteBuffer.int.toULong()
        5 -> (0uL
                or (byteBuffer.get().toUByte().toULong() shl 32)
                or (byteBuffer.int.toUInt().toULong()))
        6 -> (0uL
                or (byteBuffer.short.toUShort().toULong() shl 32)
                or (byteBuffer.int.toUInt().toULong()))
        7 -> (0uL
                or (byteBuffer.get().toUByte().toULong() shl 48)
                or (byteBuffer.short.toUShort().toULong() shl 32)
                or (byteBuffer.int.toUInt().toULong()))
        else -> byteBuffer.long.toULong()
    }
}

internal fun UByteArray.toBoolean(binaryType: SingleBooleanType?) = when (binaryType) {
    TRUE -> true
    FALSE -> false
    null, BOOLEAN1 -> this[0] !in listOf<UByte>(
        0u,
        5u,  // "F" in base64
        31u, // "f" in base64
        52u, // "0" in base64
    )
}

internal fun UByteArray.toInstant(timestampType: TimestampType?): Instant? =
    if (timestampType != null)
        toInstantFromType(timestampType, this)
    else
        toInstantFromBufferSize(this)

private fun toInstantFromType(type: TimestampType, value: UByteArray): Instant? = when (type) {
    TIMESTAMP4 -> Instant.ofEpochSecond(value.toLong())
    TIMESTAMP8 -> Instant.ofEpochMilli(value.toLong())
    TIMESTAMP12 -> Instant.ofEpochSecond(value.toLong(4), value.toLong(skipBytes = value.size - 4))
}

private fun toInstantFromBufferSize(value: UByteArray): Instant? = when {
    value.size <= 4 -> Instant.ofEpochSecond(value.toLong())
    value.size <= 8 -> Instant.ofEpochMilli(value.toLong())
    value.size <= 12 -> Instant.ofEpochSecond(value.toLong(4), value.toLong(skipBytes = value.size - 4))
    else -> error("Invalid Instant size: ${value.size}. Required 12 bytes or fewer.")
}

internal fun String.toUByteArray(charset: Charset): UByteArray =
    toByteArray(charset)
        .toUByteArray()

internal fun Instant.toUByteArray() =
    epochSecond.toBigInteger()
        .shiftLeft(32)
        .add(nano.toBigInteger())
        .toByteArray()
        .toUByteArray()

internal fun Byte.toUByteArray(): UByteArray =
    ByteBuffer.allocate(Byte.SIZE_BYTES)
        .put(this)
        .toUByteArray()

internal fun Short.toUByteArray(): UByteArray =
    ByteBuffer.allocate(Short.SIZE_BYTES)
        .putShort(this)
        .toUByteArray()

internal fun Int.toUByteArray(): UByteArray =
    ByteBuffer.allocate(Int.SIZE_BYTES)
        .putInt(this)
        .toUByteArray()

internal fun Long.toUByteArray(): UByteArray =
    ByteBuffer.allocate(Long.SIZE_BYTES)
        .putLong(this)
        .toUByteArray()


internal fun UByte.toUByteArray(): UByteArray =
    ByteBuffer.allocate(UByte.SIZE_BYTES)
        .put(this.toByte())
        .toUByteArray()

internal fun UShort.toUByteArray(): UByteArray =
    ByteBuffer.allocate(UShort.SIZE_BYTES)
        .putShort(this.toShort())
        .toUByteArray()

internal fun UInt.toUByteArray(): UByteArray =
    ByteBuffer.allocate(UInt.SIZE_BYTES)
        .putInt(this.toInt())
        .toUByteArray()

internal fun ULong.toUByteArray(): UByteArray =
    ByteBuffer.allocate(ULong.SIZE_BYTES)
        .putLong(this.toLong())
        .toUByteArray()

internal fun Float.toUByteArray(): UByteArray =
    ByteBuffer.allocate(Float.SIZE_BYTES)
        .putFloat(this)
        .toUByteArray()

internal fun Double.toUByteArray(): UByteArray =
    ByteBuffer.allocate(Double.SIZE_BYTES)
        .putDouble(this)
        .toUByteArray()

private fun ByteBuffer.toUByteArray(): UByteArray =
    this.array()
        .toUByteArray()
        .dropWhile { it == 0x0u.toUByte() }
        .toUByteArray()

private fun <T> T.repeatApply(times: Int, consumer: (T) -> Unit): T {
    for (i in 0 until times) consumer(this)
    return this
}