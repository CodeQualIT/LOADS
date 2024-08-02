@file:OptIn(ExperimentalUnsignedTypes::class)

package nl.cqit.loads.utils

import nl.cqit.loads.decode
import nl.cqit.loads.model.ARRAY_START
import nl.cqit.loads.model.BINARY_VALUE
import nl.cqit.loads.model.CONTAINER_END
import nl.cqit.loads.model.ELEMENT_SEPARATOR
import nl.cqit.loads.model.EXPECTED_ELEMENT_SEPARATOR_MSG
import nl.cqit.loads.model.INVALID_ARRAY_START_MSG
import nl.cqit.loads.model.INVALID_OBJECT_START_MSG
import nl.cqit.loads.model.OBJECT_START
import nl.cqit.loads.model.types.MultipleBooleansType
import nl.cqit.loads.model.types.ShortType.BOOLEAN1
import kotlin.reflect.KType

internal fun KType.elemType() = arguments[0].type!!
internal fun KType.keyType() = arguments[0].type!!
internal fun KType.valueType() = arguments[1].type!!

internal fun <T> toArrayContainer(
    type: KType,
    containerMapper: (MutableList<*>) -> T,
    data: UByteArray,
    offset: Int
): Pair<Int, T> {
    val elemType = type.elemType()
    return if (elemType.classifier == Boolean::class && data[offset] == BINARY_VALUE) {
        toBooleanArrayContainer(data, offset)
    } else {
        toArrayContainer(elemType, data, offset)
    }.let { (newOffset, list) -> newOffset to containerMapper(list) }
}

fun toBooleanArrayContainer(
    data: UByteArray,
    offset: Int
): Pair<Int, MutableList<*>> {
    val (newOffset, value, binaryType) = extractBinary(data, offset)
    require(value != null && value.size == 1) { "Only 1-byte values are supported for boolean array" }
    when (binaryType) {
        is BOOLEAN1 -> return newOffset to mutableListOf(value[0] and 0x01u == 1u.toUByte())
        is MultipleBooleansType -> {
            val list = mutableListOf<Boolean>()
            for (i in 0 until binaryType.suffix.digitToInt()) {
                list.add(value[0] and (1u shl i).toUByte() > 0u)
            }
            return newOffset to list
        }
        else -> error("Only !1-!6 type is supported for boolean array")
    }
}

private fun toArrayContainer(
    elemType: KType,
    data: UByteArray,
    offset: Int
): Pair<Int, MutableList<*>> {
    require(data[offset] == ARRAY_START) { INVALID_ARRAY_START_MSG + offset }
    val list = mutableListOf<Any?>()
    var i = offset + 1
    while (true) {
        val decodeElement = decode(elemType, data, i)
        i = decodeElement.first
        val element = decodeElement.second

        list.add(element)
        if (data[i] == CONTAINER_END) break
        require(data[i] == ELEMENT_SEPARATOR) { EXPECTED_ELEMENT_SEPARATOR_MSG + i }
        i++
    }
    return i + 1 to list
}

internal fun <T, U> toObjectContainer(
    keyType: KType,
    keyMapper: (Any?) -> U,
    valueTypeResolver: (U) -> KType,
    containerMapper: (MutableMap<U, *>) -> T,
    data: UByteArray,
    offset: Int
): Pair<Int, T> {
    require(data[offset] == OBJECT_START) { INVALID_OBJECT_START_MSG + offset }
    val map = mutableMapOf<U, Any?>()
    var i = offset + 1
    while (true) {
        val decodeKey = decode(keyType, data, i)
        i = decodeKey.first
        val key = keyMapper(decodeKey.second)

        require(data[i] == ELEMENT_SEPARATOR) { EXPECTED_ELEMENT_SEPARATOR_MSG + i }
        i++

        val valueType = valueTypeResolver(key)
        val decodeValue = decode(valueType, data, i)
        i = decodeValue.first
        val value = decodeValue.second

        map[key] = value
        if (data[i] == CONTAINER_END) break
        require(data[i] == ELEMENT_SEPARATOR) { EXPECTED_ELEMENT_SEPARATOR_MSG + i }
        i++
    }
    return i + 1 to containerMapper.invoke(map)
}