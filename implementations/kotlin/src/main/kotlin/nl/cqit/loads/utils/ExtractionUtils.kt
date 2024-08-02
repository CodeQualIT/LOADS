@file:OptIn(ExperimentalUnsignedTypes::class, ExperimentalStdlibApi::class)

package nl.cqit.loads.utils

import nl.cqit.loads.model.BINARY_VALUE
import nl.cqit.loads.model.INVALID_BINARY_VALUE_MSG
import nl.cqit.loads.model.INVALID_STRING_CHARACTER_MSG
import nl.cqit.loads.model.NULL_VALUE
import nl.cqit.loads.model.SPECIAL_BYTES
import nl.cqit.loads.model.VALUE_TERMINATORS
import nl.cqit.loads.model.types

internal fun extractBinary(data: UByteArray, offset: Int): Triple<Int, UByteArray?, types.BinaryType?> {
    require(data[offset] == BINARY_VALUE) { INVALID_BINARY_VALUE_MSG + offset }
    val (valueOffset, typeUBytes) = when {
        data[offset + 1] in types.BinaryType.PREDEFINED_BINARY_TYPE_CATEGORIES -> offset + 3 to data.sliceArray(offset + 1 until offset + 3)
        data[offset + 1] == types.CUSTOM_BINARY_TYPE_START -> {
            extractNextValue(data, offset + 2, ubyteArrayOf(types.CUSTOM_BINARY_TYPE_END))
                .let { (newOffset, value) -> newOffset + 1 to value }
        }

        else -> offset + 1 to null
    }
    val (newOffset, value) = extractNextValue(data, valueOffset, VALUE_TERMINATORS)
    return Triple(newOffset, value?.decodeBase64(), typeUBytes?.let(types.BinaryType.Companion::valueOf))
}

internal fun extractNextValue(data: UByteArray, offset: Int, terminators: UByteArray): Pair<Int, UByteArray?> {
    var i = offset
    while (i < data.size && data[i] !in terminators) i++
    val slice = data.sliceArray(offset until i)
    if (slice.size == 1 && slice[0] == NULL_VALUE) return i to null
    val specialCharactersInString = slice.intersect(SPECIAL_BYTES)
    require(specialCharactersInString.isEmpty()) {
        val invalidCharacters = specialCharactersInString.map {
            val hex = it.toHexString(HexFormat.UpperCase)
            val pos = slice.indexOf(it) + offset
            "Ox$hex at position $pos"
        }
        INVALID_STRING_CHARACTER_MSG + invalidCharacters.joinToString()
    }
    return i to slice
}