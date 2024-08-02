@file:OptIn(ExperimentalUnsignedTypes::class)

package nl.cqit.loads.model

import nl.cqit.loads.model.types.ShortType.*
import nl.cqit.loads.utils.toUByteArray
import java.nio.charset.StandardCharsets.UTF_8

object types {
    internal sealed class BinaryType(val typeStr: String?) {
        val type: UByteArray = typeStr?.toUByteArray(UTF_8) ?: ubyteArrayOf()

        companion object {
            val PREDEFINED_BINARY_TYPE_CATEGORIES = listOf(
                SignedIntegerType.PREFIX,
                UnsignedIntegerType.PREFIX,
                FloatingPointType.PREFIX,
                TimestampType.PREFIX,
                BooleanType.PREFIX
            ).map { it.code.toUByte() }

            fun valueOf(typeUBytes: UByteArray): BinaryType {
                val type = String(typeUBytes.toByteArray())
                return PREDEFINED_BINARY_TYPES
                    .firstOrNull { it.typeStr.equals(type, ignoreCase = true) }
                    ?: CustomType(type)
            }
        }
    }

    // Defined separately to avoid circular dependencies
    private val BinaryType.Companion.PREDEFINED_BINARY_TYPES: List<ShortType>
        get() = listOf(
            BYTE, SHORT, INT, LONG,
            UBYTE, USHORT, UINT, ULONG,
            FLOAT, DOUBLE,
            TIMESTAMP4, TIMESTAMP8, TIMESTAMP12,
            TRUE, FALSE,
            BOOLEAN1, BOOLEAN2, BOOLEAN3, BOOLEAN4, BOOLEAN5, BOOLEAN6
        )

    internal class CustomType(type: String?) : BinaryType(type?.let { "($it)" })

    internal sealed class SignedIntegerType(suffix: Char) : ShortType(PREFIX, suffix) {
        companion object {
            const val PREFIX = '#'
        }
    }

    internal sealed class UnsignedIntegerType(suffix: Char) : ShortType(PREFIX, suffix) {
        companion object {
            const val PREFIX = '+'
        }
    }

    internal sealed class FloatingPointType(suffix: Char) : ShortType(PREFIX, suffix) {
        companion object {
            const val PREFIX = '~'
        }
    }

    internal sealed class TimestampType(suffix: Char) : ShortType(PREFIX, suffix) {
        companion object {
            const val PREFIX = '@'
        }
    }

    internal sealed class BooleanType(suffix: Char) : ShortType(PREFIX, suffix) {
        companion object {
            const val PREFIX = '!'
        }
    }
    internal sealed class SingleBooleanType(suffix: Char) : BooleanType(suffix)
    internal sealed class MultipleBooleansType(val suffix: Char) : BooleanType(suffix)

    internal sealed class ShortType(val prefix: Char, suffix: Char) : BinaryType("$prefix$suffix") {
        internal data object BYTE : SignedIntegerType('1')
        internal data object SHORT : SignedIntegerType('2')
        internal data object INT : SignedIntegerType('4')
        internal data object LONG : SignedIntegerType('8')

        internal data object UBYTE : UnsignedIntegerType('1')
        internal data object USHORT : UnsignedIntegerType('2')
        internal data object UINT : UnsignedIntegerType('4')
        internal data object ULONG : UnsignedIntegerType('8')

        internal data object FLOAT : FloatingPointType('4')
        internal data object DOUBLE : FloatingPointType('8')

        internal data object TIMESTAMP4 : TimestampType('4')
        internal data object TIMESTAMP8 : TimestampType('8')
        internal data object TIMESTAMP12 : TimestampType('C')

        internal data object TRUE : SingleBooleanType('t')
        internal data object FALSE : SingleBooleanType('f')
        internal data object BOOLEAN1 : SingleBooleanType('1')

        internal data object BOOLEAN2 : MultipleBooleansType('2')
        internal data object BOOLEAN3 : MultipleBooleansType('3')
        internal data object BOOLEAN4 : MultipleBooleansType('4')
        internal data object BOOLEAN5 : MultipleBooleansType('5')
        internal data object BOOLEAN6 : MultipleBooleansType('6')
    }

    internal val CUSTOM_BINARY_TYPE_START = '('.code.toUByte()
    internal val CUSTOM_BINARY_TYPE_END = ')'.code.toUByte()
}