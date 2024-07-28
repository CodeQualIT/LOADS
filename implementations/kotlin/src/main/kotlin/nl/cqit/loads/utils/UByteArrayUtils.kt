@file:OptIn(ExperimentalUnsignedTypes::class)

package nl.cqit.loads.utils

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

internal fun UByteArray.toInt(): Int = when (size) {
    Byte.SIZE_BYTES -> toByteBuffer().get().toInt()
    Short.SIZE_BYTES -> toByteBuffer().short.toInt()
    else -> toByteBuffer().int
}

internal fun UByteArray.toLong(): Long = when (size) {
    Byte.SIZE_BYTES -> toByteBuffer().get().toLong()
    Short.SIZE_BYTES -> toByteBuffer().short.toLong()
    Int.SIZE_BYTES -> toByteBuffer().int.toLong()
    else -> toByteBuffer().long
}

internal fun UByteArray.toUByte(): UByte = toByteBuffer().get().toUByte()

internal fun UByteArray.toUShort(): UShort = when (size) {
    Byte.SIZE_BYTES -> toByteBuffer().get().toUShort()
    else -> toByteBuffer().short.toUShort()
}

internal fun UByteArray.toUInt(): UInt = when (size) {
    Byte.SIZE_BYTES -> toByteBuffer().get().toUInt()
    Short.SIZE_BYTES -> toByteBuffer().short.toUInt()
    else -> toByteBuffer().int.toUInt()
}

internal fun UByteArray.toULong(): ULong = when (size) {
    Byte.SIZE_BYTES -> toByteBuffer().get().toULong()
    Short.SIZE_BYTES -> toByteBuffer().short.toULong()
    Int.SIZE_BYTES -> toByteBuffer().int.toULong()
    else -> toByteBuffer().long.toULong()
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