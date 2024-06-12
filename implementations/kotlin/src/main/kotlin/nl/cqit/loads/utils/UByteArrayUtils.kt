@file:OptIn(ExperimentalUnsignedTypes::class)

package nl.cqit.loads.utils

import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.*
import kotlin.text.Charsets.UTF_8

internal fun String.toUByteArray(charset: Charset): UByteArray =
    toByteArray(charset).toUByteArray()


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
        .toBase64UByteArray()

internal fun UByteArray.toBase64UByteArray(): UByteArray =
    this.toBase64()
        .toUByteArray(UTF_8)

private fun UByteArray.toBase64(): String =
    Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(this.toByteArray())