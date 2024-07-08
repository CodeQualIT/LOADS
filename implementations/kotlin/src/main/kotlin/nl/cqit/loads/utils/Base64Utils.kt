@file:OptIn(ExperimentalUnsignedTypes::class)

package nl.cqit.loads.utils

import java.util.*

private val decoder = Base64.getUrlDecoder()
private val encoder = Base64.getUrlEncoder()
    .withoutPadding()

internal fun UByteArray.decodeBase64(): UByteArray =
    decoder.decode(String(toByteArray(), Charsets.UTF_8))
        .toUByteArray()

internal fun UByteArray.encodeBase64(): UByteArray =
    encoder.encodeToString(toByteArray())
        .toUByteArray(Charsets.UTF_8)
