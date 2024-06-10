@file:OptIn(ExperimentalUnsignedTypes::class)

package nl.cqit.loads

import nl.cqit.loads.utils.toUByteArray
import java.nio.charset.StandardCharsets.UTF_8

internal val INT_TYPE = "#4".toUByteArray(UTF_8)
internal val LONG_TYPE = "#8".toUByteArray(UTF_8)

internal val UINT_TYPE = "+4".toUByteArray(UTF_8)
internal val ULONG_TYPE = "+8".toUByteArray(UTF_8)

internal val FLOAT_TYPE = "~4".toUByteArray(UTF_8)
internal val DOUBLE_TYPE = "~8".toUByteArray(UTF_8)

internal val TRUE_TYPE = "!t".toUByteArray(UTF_8)
internal val FALSE_TYPE = "!f".toUByteArray(UTF_8)

internal val BOOLEAN_TYPE1 = "!1".toUByteArray(UTF_8)
internal val BOOLEAN_TYPE2 = "!2".toUByteArray(UTF_8)
internal val BOOLEAN_TYPE3 = "!3".toUByteArray(UTF_8)
internal val BOOLEAN_TYPE4 = "!4".toUByteArray(UTF_8)
internal val BOOLEAN_TYPE5 = "!5".toUByteArray(UTF_8)
internal val BOOLEAN_TYPE6 = "!6".toUByteArray(UTF_8)