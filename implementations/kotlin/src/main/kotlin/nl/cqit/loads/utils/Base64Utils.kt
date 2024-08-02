@file:OptIn(ExperimentalUnsignedTypes::class)

package nl.cqit.loads.utils

import java.util.*

private val decoder = Base64.getUrlDecoder()
private val encoder = Base64.getUrlEncoder()
    .withoutPadding()

private val REGEX_UPPER = "[A-Z]".toRegex()
private val REGEX_LOWER = "[a-z]".toRegex()
private val REGEX_DIGIT = "[0-9]".toRegex()

internal fun UByteArray.decodeBase64(): UByteArray {
    val valueString = String(toByteArray(), Charsets.UTF_8)
    if (valueString.length == 1) {
        when {
            REGEX_UPPER.matches(valueString) -> return ubyteArrayOf((valueString[0].code - 'A'.code).toUByte())
            REGEX_LOWER.matches(valueString) -> return ubyteArrayOf((valueString[0].code - 'a'.code + 26).toUByte())
            REGEX_DIGIT.matches(valueString) -> return ubyteArrayOf((valueString[0].code - '0'.code + 52).toUByte())
            valueString == "-" -> return ubyteArrayOf(62u)
            valueString == "_" -> return ubyteArrayOf(63u)
        }
    }
    return decoder.decode(valueString).toUByteArray()
}

internal fun UByteArray.encodeBase64(): UByteArray =
    encoder.encodeToString(toByteArray())
        .toUByteArray(Charsets.UTF_8)
