@file:OptIn(ExperimentalUnsignedTypes::class)

package nl.cqit.loads.model

import nl.cqit.loads.utils.toUByteArray
import java.nio.charset.StandardCharsets.UTF_8

internal val BYTE_TYPE = "#1".toUByteArray(UTF_8)
internal val SHORT_TYPE = "#2".toUByteArray(UTF_8)
internal val INT_TYPE = "#4".toUByteArray(UTF_8)
internal val LONG_TYPE = "#8".toUByteArray(UTF_8)

internal val UBYTE_TYPE = "+1".toUByteArray(UTF_8)
internal val USHORT_TYPE = "+2".toUByteArray(UTF_8)
internal val UINT_TYPE = "+4".toUByteArray(UTF_8)
internal val ULONG_TYPE = "+8".toUByteArray(UTF_8)

internal val FLOAT_TYPE = "~4".toUByteArray(UTF_8)
internal val DOUBLE_TYPE = "~8".toUByteArray(UTF_8)

internal val TIMESTAMP4_TYPE = "@4".toUByteArray(UTF_8)
internal val TIMESTAMP8_TYPE = "@8".toUByteArray(UTF_8)
internal val TIMESTAMP12_TYPE = "@C".toUByteArray(UTF_8)

internal val TRUE_TYPE = "!t".toUByteArray(UTF_8)
internal val FALSE_TYPE = "!f".toUByteArray(UTF_8)

internal val BOOLEAN_TYPE1 = "!1".toUByteArray(UTF_8)
internal val BOOLEAN_TYPE2 = "!2".toUByteArray(UTF_8)
internal val BOOLEAN_TYPE3 = "!3".toUByteArray(UTF_8)
internal val BOOLEAN_TYPE4 = "!4".toUByteArray(UTF_8)
internal val BOOLEAN_TYPE5 = "!5".toUByteArray(UTF_8)
internal val BOOLEAN_TYPE6 = "!6".toUByteArray(UTF_8)

internal val BINARY_TYPES: List<UByteArray> = listOf(
    BYTE_TYPE,
    SHORT_TYPE,
    INT_TYPE,
    LONG_TYPE,

    UBYTE_TYPE,
    USHORT_TYPE,
    UINT_TYPE,
    ULONG_TYPE,

    FLOAT_TYPE,
    DOUBLE_TYPE,

    TIMESTAMP4_TYPE,
    TIMESTAMP8_TYPE,
    TIMESTAMP12_TYPE,

    TRUE_TYPE,
    FALSE_TYPE,

    BOOLEAN_TYPE1,
    BOOLEAN_TYPE2,
    BOOLEAN_TYPE3,
    BOOLEAN_TYPE4,
    BOOLEAN_TYPE5,
    BOOLEAN_TYPE6,
)

internal val BINARY_TYPE_CATEGORIES = BINARY_TYPES.map { it.first() }.toSet()

internal val CUSTOM_BINARY_TYPE_START = '('.code.toUByte()
internal val CUSTOM_BINARY_TYPE_END = ')'.code.toUByte()