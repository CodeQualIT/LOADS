@file:OptIn(ExperimentalUnsignedTypes::class)

package nl.cqit.loads.model

import nl.cqit.loads.utils.toUByteArray
import java.nio.charset.StandardCharsets.UTF_8

internal sealed class BinaryType(binaryTypeString: String) {
    val binaryType = binaryTypeString.toUByteArray(UTF_8)
}
internal class CustomType(binaryTypeString: String): BinaryType(binaryTypeString)

internal sealed class ShortType(binaryTypeString: String): BinaryType(binaryTypeString) {
    internal sealed class SignedIntegerType(suffix: Char): ShortType("#$suffix")
    internal data object BYTE: SignedIntegerType('1')
    internal data object SHORT: SignedIntegerType('2')
    internal data object INT: SignedIntegerType('4')
    internal data object LONG: SignedIntegerType('8')
    internal sealed class UnsignedIntegerType(suffix: Char): ShortType("+$suffix")
    internal data object UBYTE: UnsignedIntegerType('1')
    internal data object USHORT: UnsignedIntegerType('2')
    internal data object UINT: UnsignedIntegerType('4')
    internal data object ULONG: UnsignedIntegerType('8')
    internal sealed class FloatingPointType(suffix: Char): ShortType("~$suffix")
    internal data object FLOAT: FloatingPointType('4')
    internal data object DOUBLE: FloatingPointType('8')
    internal sealed class TimestampType(suffix: Char): ShortType("@$suffix")
    internal data object TIMESTAMP4: TimestampType('4')
    internal data object TIMESTAMP8: TimestampType('8')
    internal data object TIMESTAMP12: TimestampType('C')
    internal sealed class BooleanType(suffix: Char): ShortType("!$suffix")
    internal data object TRUE: BooleanType('t')
    internal data object FALSE: BooleanType('f')
    internal data object BOOLEAN1: BooleanType('1')
    internal data object BOOLEAN2: BooleanType('2')
    internal data object BOOLEAN3: BooleanType('3')
    internal data object BOOLEAN4: BooleanType('4')
    internal data object BOOLEAN5: BooleanType('5')
    internal data object BOOLEAN6: BooleanType('6')

    companion object {
        private val entries = listOf(
            BYTE, SHORT, INT, LONG,
            UBYTE, USHORT, UINT, ULONG,
            FLOAT, DOUBLE,
            TIMESTAMP4, TIMESTAMP8, TIMESTAMP12,
            TRUE, FALSE,
            BOOLEAN1, BOOLEAN2, BOOLEAN3, BOOLEAN4, BOOLEAN5, BOOLEAN6
        )
        val BINARY_TYPE_CATEGORIES = entries.map { it.binaryType.first() }.toSet()
        fun valueOf(binaryType: UByteArray): ShortType? = entries.firstOrNull { it.binaryType.contentEquals(binaryType) }
    }
}

internal val CUSTOM_BINARY_TYPE_START = '('.code.toUByte()
internal val CUSTOM_BINARY_TYPE_END = ')'.code.toUByte()