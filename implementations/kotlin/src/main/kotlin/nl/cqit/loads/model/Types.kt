@file:OptIn(ExperimentalUnsignedTypes::class)

package nl.cqit.loads.model

import nl.cqit.loads.utils.toUByteArray
import java.nio.charset.StandardCharsets.UTF_8


internal enum class ShortType(binaryTypeString: String) {
    BYTE("#1"),
    SHORT("#2"),
    INT("#4"),
    LONG("#8"),

    UBYTE("+1"),
    USHORT("+2"),
    UINT("+4"),
    ULONG("+8"),

    FLOAT("~4"),
    DOUBLE("~8"),

    TIMESTAMP4("@4"),
    TIMESTAMP8("@8"),
    TIMESTAMP12("@C"),

    TRUE("!t"),
    FALSE("!f"),

    BOOLEAN1("!1"),
    BOOLEAN2("!2"),
    BOOLEAN3("!3"),
    BOOLEAN4("!4"),
    BOOLEAN5("!5"),
    BOOLEAN6("!6");

    val binaryType: UByteArray = binaryTypeString.toUByteArray(UTF_8)

    companion object {
        val BINARY_TYPE_CATEGORIES = entries.map { it.binaryType.first() }.toSet()
        fun valueOf(binaryType: UByteArray): ShortType? = entries.firstOrNull { it.binaryType.contentEquals(binaryType) }
    }
}

internal val CUSTOM_BINARY_TYPE_START = '('.code.toUByte()
internal val CUSTOM_BINARY_TYPE_END = ')'.code.toUByte()