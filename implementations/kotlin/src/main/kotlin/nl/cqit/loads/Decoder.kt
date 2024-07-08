@file:OptIn(ExperimentalUnsignedTypes::class, ExperimentalStdlibApi::class)

package nl.cqit.loads

import kotlinx.io.bytestring.ByteString
import kotlinx.io.bytestring.startsWith
import nl.cqit.loads.model.BINARY_VALUE
import nl.cqit.loads.model.INVALID_BINARY_VALUE_MSG
import nl.cqit.loads.model.INVALID_STRING_CHARACTER_MSG
import nl.cqit.loads.model.SPECIAL_BYTES
import nl.cqit.loads.model.BINARY_TYPES
import nl.cqit.loads.model.VALUE_TERMINATORS
import nl.cqit.loads.utils.andThen
import nl.cqit.loads.utils.cast
import nl.cqit.loads.utils.decodeBase64
import nl.cqit.loads.utils.elemType
import nl.cqit.loads.utils.get
import nl.cqit.loads.utils.isData
import nl.cqit.loads.utils.keyType
import nl.cqit.loads.utils.toArrayContainer
import nl.cqit.loads.utils.toObjectContainer
import nl.cqit.loads.utils.valueType
import kotlin.reflect.*
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.primaryConstructor

inline fun <reified T : Any> decode(data: UByteArray): T {
    val type = typeOf<T>()
    return decode(type, data, 0).second as T
}

fun decode(type: KType, data: UByteArray, offset: Int): Pair<Int, *> {
    val (newOffset: Int, result: Any?) = when {
        type.isSubtypeOf(typeOf<Array<*>>()) -> toArray(type, data, offset)
        type.classifier == List::class -> toList(type, data, offset)
        type.classifier == Set::class -> toSet(type, data, offset)
        type.classifier == Collection::class -> toCollection(type, data, offset)
        type.classifier == Map::class -> toMap(type, data, offset)
        type.classifier == String::class -> toString(data, offset)
        isData(type.classifier) -> toData(type, data, offset)
        type.classifier == UByteArray::class -> toUByteArray(type, data, offset)
//        ByteArray::class -> TODO()
//        String::class -> TODO()
//        Byte::class -> TODO()
//        Short::class -> TODO()
//        Int::class -> TODO()
//        Long::class -> TODO()
//        UByte::class -> TODO()
//        UShort::class -> TODO()
//        UInt::class -> TODO()
//        ULong::class -> TODO()
//        Float::class -> TODO()
//        Double::class -> TODO()
//        Boolean::class -> TODO()
//        Instant::class -> TODO()
//        OffsetDateTime::class -> TODO()
//        ZonedDateTime::class -> TODO()
        else -> throw NotImplementedError()
    }
    return newOffset to result
}

private fun toUByteArray(type: KType, data: UByteArray, offset: Int): Pair<Int, Any?> {
    require(data[offset] == BINARY_VALUE) { INVALID_BINARY_VALUE_MSG + offset }
    var (newOffset, value) = extractNextValue(data, offset + 1)
    val binaryType = BINARY_TYPES.filter { ByteString(value.toByteArray()).startsWith(it.toByteArray()) }
        .firstOrNull()
    if (binaryType != null) {
        value = value.sliceArray(binaryType.size until value.size)
    }
    return newOffset to value.decodeBase64()
}

private fun toString(data: UByteArray, offset: Int): Pair<Int, String> {
    val (newOffset, value) = extractNextValue(data, offset)
    return newOffset to String(value.toByteArray())
}

private fun toArray(containerType: KType, data: UByteArray, offset: Int): Pair<Int, Array<*>> =
    toArrayContainer(containerType, MutableList<*>::toTypedArray.andThen { cast(containerType.elemType()) }, data, offset)

private fun toList(containerType: KType, data: UByteArray, offset: Int): Pair<Int, List<*>> =
    toArrayContainer(containerType, MutableList<*>::toList, data, offset)

private fun toSet(containerType: KType, data: UByteArray, offset: Int): Pair<Int, Set<*>> =
    toArrayContainer(containerType, MutableList<*>::toSet, data, offset)

private fun toCollection(containerType: KType, data: UByteArray, offset: Int): Pair<Int, Collection<*>> =
    toArrayContainer(containerType, MutableList<*>::toList, data, offset)

private fun toMap(containerType: KType, data: UByteArray, offset: Int): Pair<Int, Map<*, *>> {
    return toObjectContainer(
        containerType.keyType(), { it }, { containerType.valueType() },
        MutableMap<*, *>::toMap, data, offset
    )
}

private fun toData(type: KType, data: UByteArray, offset: Int): Pair<Int, *> {
    val dataClass = type.classifier as KClass<*>
    val constructor = dataClass.primaryConstructor!!
    return toObjectContainer(
        typeOf<String>(), constructor.parameters::get, KParameter::type,
        constructor::callBy, data, offset
    )
}

private fun extractNextValue(data: UByteArray, offset: Int): Pair<Int, UByteArray> {
    var i = offset
    while (i < data.size && data[i] !in VALUE_TERMINATORS) i++
    val slice = data.sliceArray(offset until i)
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


