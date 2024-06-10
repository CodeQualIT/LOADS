package nl.cqit.loads.utils

import kotlin.reflect.full.memberProperties


internal fun <T : Any> T.getProperties() = this::class.memberProperties
    .associate { it.name to it.getter.call(this) }