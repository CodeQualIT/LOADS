@file:OptIn(ExperimentalUnsignedTypes::class)

package nl.cqit.loads.utils

import kotlin.reflect.KFunction
import kotlin.reflect.full.functions
import kotlin.reflect.full.valueParameters

internal fun callToByteArrayMethod(kotlinObject: Any) =
    kotlinObject.javaClass.kotlin.functions
        .first(::containsToByteArrayFunction)
        .call(kotlinObject) as ByteArray

internal fun hasToByteArrayMethod(kotlinObject: Any): Boolean =
    kotlinObject.javaClass.kotlin.functions
        .any(::containsToByteArrayFunction)

private fun containsToByteArrayFunction(it: KFunction<*>) =
    it.name == "toByteArray"
            && it.valueParameters.isEmpty()
            && it.returnType.classifier == ByteArray::class

internal fun callToUByteArrayMethod(kotlinObject: Any) =
    kotlinObject.javaClass.kotlin.functions
        .first(::containsToUByteArrayFunction)
        .call(kotlinObject) as UByteArray

internal fun hasToUByteArrayMethod(kotlinObject: Any): Boolean =
    kotlinObject.javaClass.kotlin.functions
        .any(::containsToUByteArrayFunction)


private fun containsToUByteArrayFunction(it: KFunction<*>) =
    it.name == "toUByteArray"
            && it.valueParameters.isEmpty()
            && it.returnType.classifier == UByteArray::class