@file:OptIn(ExperimentalUnsignedTypes::class)

package nl.cqit.loads.model


internal const val ARRAY_START: UByte = 0xFAu
internal const val BINARY_VALUE: UByte = 0xFBu
internal const val OBJECT_START: UByte = 0xFCu
internal const val NULL_VALUE: UByte = 0xFDu
internal const val CONTAINER_END: UByte = 0xFEu
internal const val ELEMENT_SEPARATOR: UByte = 0xFFu

internal val SPECIAL_BYTES = ubyteArrayOf(
    ARRAY_START,
    BINARY_VALUE,
    OBJECT_START,
    NULL_VALUE,
    CONTAINER_END,
    ELEMENT_SEPARATOR
)
internal val VALUE_TERMINATORS = ubyteArrayOf(
    ELEMENT_SEPARATOR,
    CONTAINER_END
)
