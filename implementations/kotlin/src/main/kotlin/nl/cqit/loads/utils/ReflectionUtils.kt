@file:OptIn(ExperimentalUnsignedTypes::class)

package nl.cqit.loads.utils

import kotlin.reflect.KClassifier
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.functions
import kotlin.reflect.full.valueParameters

internal operator fun List<KParameter>.get(key: Any?): KParameter =
    firstOrNull { it.name == key }
        ?: throw IllegalArgumentException("Unknown key found: $key")

internal fun callToByteArrayMethod(kotlinObject: Any) =
    kotlinObject.javaClass.kotlin.functions
        .first(::containsToByteArrayFunction)
        .call(kotlinObject) as ByteArray

internal fun callToUByteArrayMethod(kotlinObject: Any) =
    kotlinObject.javaClass.kotlin.functions
        .first(::containsToUByteArrayFunction)
        .call(kotlinObject) as UByteArray

internal fun hasToByteArrayMethod(kotlinObject: Any): Boolean =
    kotlinObject.javaClass.kotlin.functions
        .any(::containsToByteArrayFunction)

internal fun hasToUByteArrayMethod(kotlinObject: Any): Boolean =
    kotlinObject.javaClass.kotlin.functions
        .any(::containsToUByteArrayFunction)

private fun containsToByteArrayFunction(it: KFunction<*>) =
    it.hasSignature("toByteArray", emptyList(), ByteArray::class)

private fun containsToUByteArrayFunction(it: KFunction<*>) =
    it.hasSignature("toUByteArray", emptyList(), UByteArray::class)

private fun KFunction<*>.hasSignature(fnName: String, params: List<KParameter>, returnCls: KClassifier) =
    name == fnName
            && valueParameters == params
            && returnType.classifier == returnCls