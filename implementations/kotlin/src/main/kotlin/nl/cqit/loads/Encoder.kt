@file:OptIn(ExperimentalUnsignedTypes::class)

package nl.cqit.loads

import nl.cqit.loads.utils.*
import nl.cqit.loads.utils.getProperties
import nl.cqit.loads.utils.toBase64UByteArray
import nl.cqit.loads.utils.toUByteArray
import java.nio.charset.StandardCharsets.UTF_8

fun Any?.toLoads(): UByteArray = from(this)

fun from(kotlinObject: Any?): UByteArray {
    return when (kotlinObject) {
        null -> fromNull()
        is String -> fromString(kotlinObject)
        is Array<*> -> fromArray(kotlinObject)
        kotlinObject.takeIf { it::class.isData} -> fromDataClass(kotlinObject)
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
        kotlinObject.takeIf { hasToUByteArrayMethod(it) } ->
            fromUByteArray(kotlinObject.javaClass.simpleName, callToUByteArrayMethod(kotlinObject))
        kotlinObject.takeIf { hasToByteArrayMethod(it) } ->
            fromByteArray(kotlinObject.javaClass.simpleName, callToByteArrayMethod(kotlinObject))
        else -> throw IllegalArgumentException("Unsupported type: ${kotlinObject::class}")
    }
}


private fun fromArray(array: Array<*>) = ubyteArrayOf(
    0xFAu, *fromArrayElements(array), 0xFEu
)

private fun fromArrayElements(array: Array<*>): UByteArray =
    array.map { from(it!!) }
        .reduce { acc, bytes -> ubyteArrayOf(*acc, 0xFFu, *bytes) }

private fun <T: Any> fromDataClass(dataClass: T): UByteArray = ubyteArrayOf(
    0xFCu, *fromDataClassProperties(dataClass.getProperties()), 0xFEu
)

private fun fromDataClassProperties(properties: Map<String, Any?>): UByteArray =
    properties.map { (name, value) -> ubyteArrayOf(*from(name), 0xFFu, *from(value)) }
        .reduce { acc, bytes -> ubyteArrayOf(*acc, 0xFFu, *bytes) }

private fun fromNull() = ubyteArrayOf(0xFDu)
private fun fromString(string: String) = string.toUByteArray(UTF_8)

private fun fromByte(byte: Byte): UByteArray = ubyteArrayOf(0xFBu,  *BYTE_TYPE, *byte.toUByteArray())
private fun fromShort(short: Short): UByteArray = ubyteArrayOf(0xFBu,  *SHORT_TYPE, *short.toUByteArray())
private fun fromInt(int: Int): UByteArray = ubyteArrayOf(0xFBu,  *INT_TYPE, *int.toUByteArray())
private fun fromLong(long: Long): UByteArray = ubyteArrayOf(0xFBu,  *LONG_TYPE, *long.toUByteArray())

private fun fromUByte(uByte: UByte): UByteArray = ubyteArrayOf(0xFBu,  *UBYTE_TYPE, *uByte.toUByteArray())
private fun fromUShort(uShort: UShort): UByteArray = ubyteArrayOf(0xFBu,  *USHORT_TYPE, *uShort.toUByteArray())
private fun fromUInt(uInt: UInt): UByteArray = ubyteArrayOf(0xFBu,  *UINT_TYPE, *uInt.toUByteArray())
private fun fromULong(uLong: ULong): UByteArray = ubyteArrayOf(0xFBu,  *ULONG_TYPE, *uLong.toUByteArray())

private fun fromFloat(float: Float): UByteArray = ubyteArrayOf(0xFBu,  *FLOAT_TYPE, *float.toUByteArray())
private fun fromDouble(double: Double): UByteArray = ubyteArrayOf(0xFBu,  *DOUBLE_TYPE, *double.toUByteArray())

private fun fromBoolean(boolean: Boolean): UByteArray = ubyteArrayOf(0xFBu, *if (boolean) TRUE_TYPE else FALSE_TYPE)

fun ByteArray.toLoads(type: String): UByteArray = fromByteArray(type, this)
fun fromUByteArray(type: String, byteArray: UByteArray): UByteArray = ubyteArrayOf(
    0xFBu, *("(${type})").toUByteArray(UTF_8), *byteArray.toBase64UByteArray()
)

fun UByteArray.toLoads(type: String): UByteArray = fromUByteArray(type, this)
fun fromByteArray(type: String, byteArray: ByteArray): UByteArray = ubyteArrayOf(
    0xFBu, *("(${type})").toUByteArray(UTF_8), *byteArray.toUByteArray().toBase64UByteArray()
)
