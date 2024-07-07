@file:OptIn(ExperimentalUnsignedTypes::class)

package nl.cqit.loads.utils

import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.cast
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

internal fun Array<*>.cast(subType: KType): Array<*> = when {
    subType.isSubtypeOf(typeOf<Array<*>>()) -> map { it as Array<*> }
        .map { cast(subType.elemType()) }
        .toTypedArray()

    subType.classifier == List::class -> map { it as List<*> }.toTypedArray()
    subType.classifier == Set::class -> map { it as Set<*> }.toTypedArray()
    subType.classifier == Collection::class -> map { it as Collection<*> }.toTypedArray()
    subType.classifier == Map::class -> map { it as Map<*, *> }.toTypedArray()
    subType.classifier == String::class -> map { it as String }.toTypedArray()
    isData(subType.classifier) -> map((subType.classifier!! as KClass<*>)::cast).toTypedArray()
    subType.classifier == ByteArray::class -> map { it as ByteArray }.toTypedArray()
    subType.classifier == UByteArray::class -> map { it as UByteArray }.toTypedArray()
    subType.classifier == String::class -> map { it as String }.toTypedArray()
    subType.classifier == Byte::class -> map { it as Byte }.toTypedArray()
    subType.classifier == Short::class -> map { it as Short }.toTypedArray()
    subType.classifier == Int::class -> map { it as Int }.toTypedArray()
    subType.classifier == Long::class -> map { it as Long }.toTypedArray()
    subType.classifier == UByte::class -> map { it as UByte }.toTypedArray()
    subType.classifier == UShort::class -> map { it as UShort }.toTypedArray()
    subType.classifier == UInt::class -> map { it as UInt }.toTypedArray()
    subType.classifier == ULong::class -> map { it as ULong }.toTypedArray()
    subType.classifier == Float::class -> map { it as Float }.toTypedArray()
    subType.classifier == Double::class -> map { it as Double }.toTypedArray()
    subType.classifier == Boolean::class -> map { it as Boolean }.toTypedArray()
    subType.classifier == Instant::class -> map { it as Instant }.toTypedArray()
    subType.classifier == OffsetDateTime::class -> map { it as OffsetDateTime }.toTypedArray()
    subType.classifier == ZonedDateTime::class -> map { it as ZonedDateTime }.toTypedArray()
    else -> throw NotImplementedError()
}