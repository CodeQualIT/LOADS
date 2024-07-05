@file:OptIn(ExperimentalUnsignedTypes::class)

package nl.cqit.loads

import nl.cqit.loads.utils.callToByteArrayMethod
import nl.cqit.loads.utils.callToUByteArrayMethod
import nl.cqit.loads.utils.getProperties
import nl.cqit.loads.utils.hasToByteArrayMethod
import nl.cqit.loads.utils.hasToUByteArrayMethod
import nl.cqit.loads.utils.toBase64UByteArray
import nl.cqit.loads.utils.toUByteArray
import java.nio.charset.StandardCharsets.UTF_8
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZonedDateTime

fun Any?.toLoads(): UByteArray = from(this)

fun from(kotlinObject: Any?): UByteArray {
    return when (kotlinObject) {
        null -> fromNull()
        is String -> fromString(kotlinObject)
        is Array<*> -> fromArray(kotlinObject)
        is Collection<*> -> fromArray(kotlinObject.toTypedArray())
        kotlinObject.takeIf { it::class.isData } -> fromDataClass(kotlinObject)
        is Byte -> fromByte(kotlinObject)
        is Short -> fromShort(kotlinObject)
        is Int -> fromInt(kotlinObject)
        is Long -> fromLong(kotlinObject)
        is UByte -> fromUByte(kotlinObject)
        is UShort -> fromUShort(kotlinObject)
        is UInt -> fromUInt(kotlinObject)
        is ULong -> fromULong(kotlinObject)
        is Float -> fromFloat(kotlinObject)
        is Double -> fromDouble(kotlinObject)
        is Boolean -> fromBoolean(kotlinObject)
        is Instant -> fromInstant(kotlinObject)
        is OffsetDateTime -> fromOffsetDateTime(kotlinObject)
        is ZonedDateTime -> fromZonedDateTime(kotlinObject)
        kotlinObject.takeIf { hasToUByteArrayMethod(it) } ->
            fromUByteArray(kotlinObject.javaClass.simpleName, callToUByteArrayMethod(kotlinObject))

        kotlinObject.takeIf { hasToByteArrayMethod(it) } ->
            fromByteArray(kotlinObject.javaClass.simpleName, callToByteArrayMethod(kotlinObject))

        else -> throw IllegalArgumentException("Unsupported type: ${kotlinObject::class}")
    }
}

private fun fromArray(array: Array<*>) = ubyteArrayOf(
    ARRAY_START, *fromArrayElements(array), CONTAINER_END
)

private fun fromArrayElements(array: Array<*>): UByteArray =
    array.map { from(it!!) }
        .reduce { acc, bytes -> ubyteArrayOf(*acc, ELEMENT_SEPARATOR, *bytes) }

private fun <T : Any> fromDataClass(dataClass: T): UByteArray = ubyteArrayOf(
    OBJECT_START, *fromDataClassProperties(dataClass.getProperties()), CONTAINER_END
)

private fun fromDataClassProperties(properties: Map<String, Any?>): UByteArray =
    properties.map { (name, value) -> ubyteArrayOf(*from(name), ELEMENT_SEPARATOR, *from(value)) }
        .reduce { acc, bytes -> ubyteArrayOf(*acc, ELEMENT_SEPARATOR, *bytes) }

private fun fromNull() =
    ubyteArrayOf(NULL_VALUE)

private fun fromString(string: String) =
    string.toUByteArray(UTF_8)

private fun fromByte(byte: Byte): UByteArray =
    ubyteArrayOf(BINARY_VALUE, *BYTE_TYPE, *byte.toUByteArray())

private fun fromShort(short: Short): UByteArray =
    ubyteArrayOf(BINARY_VALUE, *SHORT_TYPE, *short.toUByteArray())

private fun fromInt(int: Int): UByteArray =
    ubyteArrayOf(BINARY_VALUE, *INT_TYPE, *int.toUByteArray())

private fun fromLong(long: Long): UByteArray =
    ubyteArrayOf(BINARY_VALUE, *LONG_TYPE, *long.toUByteArray())

private fun fromUByte(uByte: UByte): UByteArray =
    ubyteArrayOf(BINARY_VALUE, *UBYTE_TYPE, *uByte.toUByteArray())

private fun fromUShort(uShort: UShort): UByteArray =
    ubyteArrayOf(BINARY_VALUE, *USHORT_TYPE, *uShort.toUByteArray())

private fun fromUInt(uInt: UInt): UByteArray =
    ubyteArrayOf(BINARY_VALUE, *UINT_TYPE, *uInt.toUByteArray())

private fun fromULong(uLong: ULong): UByteArray =
    ubyteArrayOf(BINARY_VALUE, *ULONG_TYPE, *uLong.toUByteArray())

private fun fromFloat(float: Float): UByteArray =
    ubyteArrayOf(BINARY_VALUE, *FLOAT_TYPE, *float.toUByteArray())

private fun fromDouble(double: Double): UByteArray =
    ubyteArrayOf(BINARY_VALUE, *DOUBLE_TYPE, *double.toUByteArray())

private fun fromBoolean(boolean: Boolean): UByteArray =
    ubyteArrayOf(BINARY_VALUE, *if (boolean) TRUE_TYPE else FALSE_TYPE)

fun fromInstant(instant: Instant ): UByteArray {
    val data = instant.toBigInteger().toByteArray().toUByteArray()
    return ubyteArrayOf(BINARY_VALUE, *TIMESTAMP12_TYPE, *data.toBase64UByteArray())
}

private fun Instant.toBigInteger() =
    epochSecond.toBigInteger().shiftLeft(32) + nano.toBigInteger()

fun fromOffsetDateTime(odt: OffsetDateTime): UByteArray = fromInstant(odt.toInstant())
fun fromZonedDateTime(zdt: ZonedDateTime): UByteArray = fromInstant(zdt.toInstant())

fun ByteArray.toLoads(type: String): UByteArray = fromByteArray(type, this)
fun fromUByteArray(type: String, byteArray: UByteArray): UByteArray = ubyteArrayOf(
    BINARY_VALUE, *("(${type})").toUByteArray(UTF_8), *byteArray.toBase64UByteArray()
)

fun UByteArray.toLoads(type: String): UByteArray = fromUByteArray(type, this)
fun fromByteArray(type: String, byteArray: ByteArray): UByteArray = ubyteArrayOf(
    BINARY_VALUE, *("(${type})").toUByteArray(UTF_8), *byteArray.toUByteArray().toBase64UByteArray()
)
