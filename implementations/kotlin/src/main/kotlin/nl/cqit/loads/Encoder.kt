@file:OptIn(ExperimentalUnsignedTypes::class)

package nl.cqit.loads

import nl.cqit.loads.model.ARRAY_START
import nl.cqit.loads.model.BINARY_VALUE
import nl.cqit.loads.model.CONTAINER_END
import nl.cqit.loads.model.ELEMENT_SEPARATOR
import nl.cqit.loads.model.NULL_VALUE
import nl.cqit.loads.model.OBJECT_START
import nl.cqit.loads.model.ShortType.*
import nl.cqit.loads.utils.callToByteArrayMethod
import nl.cqit.loads.utils.callToUByteArrayMethod
import nl.cqit.loads.utils.getProperties
import nl.cqit.loads.utils.hasToByteArrayMethod
import nl.cqit.loads.utils.hasToUByteArrayMethod
import nl.cqit.loads.utils.encodeBase64
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
    ubyteArrayOf(BINARY_VALUE, *BYTE.binaryType, *byte.toUByteArray().encodeBase64())

private fun fromShort(short: Short): UByteArray =
    ubyteArrayOf(BINARY_VALUE, *SHORT.binaryType, *short.toUByteArray().encodeBase64())

private fun fromInt(int: Int): UByteArray =
    ubyteArrayOf(BINARY_VALUE, *INT.binaryType, *int.toUByteArray().encodeBase64())

private fun fromLong(long: Long): UByteArray =
    ubyteArrayOf(BINARY_VALUE, *LONG.binaryType, *long.toUByteArray().encodeBase64())

private fun fromUByte(uByte: UByte): UByteArray =
    ubyteArrayOf(BINARY_VALUE, *UBYTE.binaryType, *uByte.toUByteArray().encodeBase64())

private fun fromUShort(uShort: UShort): UByteArray =
    ubyteArrayOf(BINARY_VALUE, *USHORT.binaryType, *uShort.toUByteArray().encodeBase64())

private fun fromUInt(uInt: UInt): UByteArray =
    ubyteArrayOf(BINARY_VALUE, *UINT.binaryType, *uInt.toUByteArray().encodeBase64())

private fun fromULong(uLong: ULong): UByteArray =
    ubyteArrayOf(BINARY_VALUE, *ULONG.binaryType, *uLong.toUByteArray().encodeBase64())

private fun fromFloat(float: Float): UByteArray =
    ubyteArrayOf(BINARY_VALUE, *FLOAT.binaryType, *float.toUByteArray().encodeBase64())

private fun fromDouble(double: Double): UByteArray =
    ubyteArrayOf(BINARY_VALUE, *DOUBLE.binaryType, *double.toUByteArray().encodeBase64())

private fun fromBoolean(boolean: Boolean): UByteArray {
    val binaryType = if (boolean) TRUE.binaryType else FALSE.binaryType
    return ubyteArrayOf(BINARY_VALUE, *binaryType)
}

fun fromInstant(instant: Instant ): UByteArray =
    ubyteArrayOf(BINARY_VALUE, *TIMESTAMP12.binaryType, *instant.toUByteArray().encodeBase64())

fun fromOffsetDateTime(odt: OffsetDateTime): UByteArray = fromInstant(odt.toInstant())
fun fromZonedDateTime(zdt: ZonedDateTime): UByteArray = fromInstant(zdt.toInstant())

fun ByteArray.toLoads(type: String): UByteArray = fromByteArray(type, this)
fun fromUByteArray(type: String, byteArray: UByteArray): UByteArray = ubyteArrayOf(
    BINARY_VALUE, *("(${type})").toUByteArray(UTF_8), *byteArray.encodeBase64()
)

fun UByteArray.toLoads(type: String): UByteArray = fromUByteArray(type, this)
fun fromByteArray(type: String, byteArray: ByteArray): UByteArray = ubyteArrayOf(
    BINARY_VALUE, *("(${type})").toUByteArray(UTF_8), *byteArray.toUByteArray().encodeBase64()
)
