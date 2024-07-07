@file:OptIn(ExperimentalUnsignedTypes::class)

package nl.cqit.loads.utils

import java.util.*

private val encoder = Base64.getUrlEncoder()
    .withoutPadding()

internal fun UByteArray.encodeBase64(): UByteArray =
    encoder.encodeToString(this.toByteArray())
        .toUByteArray(Charsets.UTF_8)
