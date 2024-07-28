@file:OptIn(ExperimentalUnsignedTypes::class)

package nl.cqit.loads

import nl.cqit.loads.model.ARRAY_START
import nl.cqit.loads.model.BINARY_VALUE
import nl.cqit.loads.model.CONTAINER_END
import nl.cqit.loads.model.ELEMENT_SEPARATOR
import nl.cqit.loads.model.OBJECT_START
import nl.cqit.loads.model.types.ShortType.*
import nl.cqit.loads.utils.toUByteArray
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.Test
import kotlin.text.Charsets.UTF_8

class DecoderKtTest {

    @Test
    fun `decode String`() {
        // prepare
        val input = ubyteArrayOf(0x31u, 0x32u, 0x33u)

        // execute
        val actual: String = decode(input)

        // verify
        val expected = "123"
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode UByteArray`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"AQID".toUByteArray(UTF_8)
        )

        // execute
        val actual: UByteArray = decode(input)

        // verify
        val expected = ubyteArrayOf(0x01u, 0x02u, 0x03u)
        assertThat(actual).containsSequence(expected)
    }

    @Test
    fun `decode uByteArray with int type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *INT.type,
            *"AQID".toUByteArray(UTF_8)
        )

        // execute
        val actual: UByteArray = decode(input)

        // verify
        val expected = ubyteArrayOf(0x01u, 0x02u, 0x03u)
        assertThat(actual).containsSequence(expected)
    }

    @Test
    fun `decode UByteArray with custom type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"(mycustomtype)".toUByteArray(UTF_8),
            *"AQID".toUByteArray(UTF_8)
        )

        // execute
        val actual: UByteArray = decode(input)

        // verify
        val expected = ubyteArrayOf(0x01u, 0x02u, 0x03u)
        assertThat(actual).containsSequence(expected)
    }

    @Test
    fun `decode ByteArray`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"AQID".toUByteArray(UTF_8)
        )

        // execute
        val actual: ByteArray = decode(input)

        // verify
        val expected = byteArrayOf(0x01, 0x02, 0x03)
        assertThat(actual).containsExactly(*expected)
    }

    @Test
    fun `decode ByteArray with int type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *INT.type,
            *"AQID".toUByteArray(UTF_8)
        )

        // execute
        val actual: ByteArray = decode(input)

        // verify
        val expected = byteArrayOf(0x01, 0x02, 0x03)
        assertThat(actual).containsExactly(*expected)
    }

    @Test
    fun `decode ByteArray with custom type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"(mycustomtype)".toUByteArray(UTF_8),
            *"AQID".toUByteArray(UTF_8)
        )

        // execute
        val actual: ByteArray = decode(input)

        // verify
        val expected = byteArrayOf(0x01, 0x02, 0x03)
        assertThat(actual).containsExactly(*expected)
    }

    @Test
    fun `decode Byte`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"ew".toUByteArray(UTF_8)
        )

        // execute
        val actual: Byte = decode(input)

        // verify
        val expected = 123.toByte()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode Byte with type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *BYTE.type,
            *"ew".toUByteArray(UTF_8)
        )

        // execute
        val actual: Byte = decode(input)

        // verify
        val expected = 123.toByte()
        assertThat(actual).isEqualTo(expected)
    }

    //TODO implement custom type decoding for Byte et all
//    @Test
//    fun `decode Byte with custom type`() {
//        // prepare
//        val input = ubyteArrayOf(
//            BINARY_VALUE,
//            *"(i8)".toUByteArray(UTF_8),
//            *"ew".toUByteArray(UTF_8)
//        )
//
//        // execute
//        val actual: Byte = decode(input)
//
//        // verify
//        val expected = 123.toByte()
//        assertThat(actual).isEqualTo(expected)
//    }

    @Test
    fun `decode Byte with wrong type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *INT.type,
            *"ew".toUByteArray(UTF_8)
        )

        // execute and verify
        assertThatIllegalArgumentException()
            .isThrownBy { decode<Byte>(input) }
            .withMessage("Expected Byte but got INT")
    }

    @Test
    fun `decode Short`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"MDk".toUByteArray(UTF_8)
        )

        // execute
        val actual: Short = decode(input)

        // verify
        val expected = 12345.toShort()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode Short with type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *SHORT.type,
            *"MDk".toUByteArray(UTF_8)
        )

        // execute
        val actual: Short = decode(input)

        // verify
        val expected = 12345.toShort()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode Short with wrong type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *INT.type,
            *"MDk".toUByteArray(UTF_8)
        )

        // execute and verify
        assertThatIllegalArgumentException()
            .isThrownBy { decode<Short>(input) }
            .withMessage("Expected Short but got INT")
    }

    @Test
    fun `decode Short with 1 byte`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"ew".toUByteArray(UTF_8)
        )

        // execute
        val actual: Short = decode(input)

        // verify
        val expected = 123.toShort()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode Int`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"SZYC0g".toUByteArray(UTF_8)
        )

        // execute
        val actual: Int = decode(input)

        // verify
        val expected = 1234567890
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode Int with type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *INT.type,
            *"SZYC0g".toUByteArray(UTF_8)
        )

        // execute
        val actual: Int = decode(input)

        // verify
        val expected = 1234567890
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode Int with wrong type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *LONG.type,
            *"SZYC0g".toUByteArray(UTF_8)
        )

        // execute and verify
        assertThatIllegalArgumentException()
            .isThrownBy { decode<Int>(input) }
            .withMessage("Expected Int but got LONG")
    }

    @Test
    fun `decode Int with 1 byte`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"ew".toUByteArray(UTF_8)
        )

        // execute
        val actual: Int = decode(input)

        // verify
        val expected = 123
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode Int with 2 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"MDk".toUByteArray(UTF_8)
        )

        // execute
        val actual: Int = decode(input)

        // verify
        val expected = 12345
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode Long`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"ESIQ9H3pgRU".toUByteArray(UTF_8)
        )

        // execute
        val actual: Long = decode(input)

        // verify
        val expected = 1234567890123456789L
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode Long with type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *LONG.type,
            *"ESIQ9H3pgRU".toUByteArray(UTF_8)
        )

        // execute
        val actual: Long = decode(input)

        // verify
        val expected = 1234567890123456789L
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode Long with wrong type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *INT.type,
            *"ESIQ9H3pgRU".toUByteArray(UTF_8)
        )

        // execute and verify
        assertThatIllegalArgumentException()
            .isThrownBy { decode<Long>(input) }
            .withMessage("Expected Long but got INT")
    }

    @Test
    fun `decode Long with 1 byte`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"ew".toUByteArray(UTF_8)
        )

        // execute
        val actual: Long = decode(input)

        // verify
        val expected = 123L
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode Long with 2 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"MDk".toUByteArray(UTF_8)
        )

        // execute
        val actual: Long = decode(input)

        // verify
        val expected = 12345L
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode Long with 4 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"SZYC0g".toUByteArray(UTF_8)
        )

        // execute
        val actual: Long = decode(input)

        // verify
        val expected = 1234567890L
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode UByte`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"ew".toUByteArray(UTF_8)
        )

        // execute
        val actual: UByte = decode(input)

        // verify
        val expected = 123.toUByte()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode UByte with type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *UBYTE.type,
            *"ew".toUByteArray(UTF_8)
        )

        // execute
        val actual: UByte = decode(input)

        // verify
        val expected = 123.toUByte()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode UByte with wrong type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *INT.type,
            *"ew".toUByteArray(UTF_8)
        )

        // execute and verify
        assertThatIllegalArgumentException()
            .isThrownBy { decode<UByte>(input) }
            .withMessage("Expected UByte but got INT")
    }

    @Test
    fun `decode UShort`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"MDk".toUByteArray(UTF_8)
        )

        // execute
        val actual: UShort = decode(input)

        // verify
        val expected = 12345.toUShort()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode UShort with type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *USHORT.type,
            *"MDk".toUByteArray(UTF_8)
        )

        // execute
        val actual: UShort = decode(input)

        // verify
        val expected = 12345.toUShort()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode UShort with wrong type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *INT.type,
            *"MDk".toUByteArray(UTF_8)
        )

        // execute and verify
        assertThatIllegalArgumentException()
            .isThrownBy { decode<UShort>(input) }
            .withMessage("Expected UShort but got INT")
    }

    @Test
    fun `decode UShort with 1 byte`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"ew".toUByteArray(UTF_8)
        )

        // execute
        val actual: UShort = decode(input)

        // verify
        val expected = 123.toUShort()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode UInt`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"SZYC0g".toUByteArray(UTF_8)
        )

        // execute
        val actual: UInt = decode(input)

        // verify
        val expected = 1234567890.toUInt()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode UInt with type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *UINT.type,
            *"SZYC0g".toUByteArray(UTF_8)
        )

        // execute
        val actual: UInt = decode(input)

        // verify
        val expected = 1234567890.toUInt()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode UInt with wrong type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *LONG.type,
            *"SZYC0g".toUByteArray(UTF_8)
        )

        // execute and verify
        assertThatIllegalArgumentException()
            .isThrownBy { decode<UInt>(input) }
            .withMessage("Expected UInt but got LONG")
    }

    @Test
    fun `decode UInt with 1 byte`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"ew".toUByteArray(UTF_8)
        )

        // execute
        val actual: UInt = decode(input)

        // verify
        val expected = 123.toUInt()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode UInt with 2 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"MDk".toUByteArray(UTF_8)
        )

        // execute
        val actual: UInt = decode(input)

        // verify
        val expected = 12345.toUInt()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode ULong`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"ESIQ9H3pgRU".toUByteArray(UTF_8)
        )

        // execute
        val actual: ULong = decode(input)

        // verify
        val expected = 1234567890123456789L.toULong()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode ULong with type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *ULONG.type,
            *"ESIQ9H3pgRU".toUByteArray(UTF_8)
        )

        // execute
        val actual: ULong = decode(input)

        // verify
        val expected = 1234567890123456789L.toULong()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode ULong with wrong type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *INT.type,
            *"ESIQ9H3pgRU".toUByteArray(UTF_8)
        )

        // execute and verify
        assertThatIllegalArgumentException()
            .isThrownBy { decode<ULong>(input) }
            .withMessage("Expected ULong but got INT")
    }

    @Test
    fun `decode ULong with 1 byte`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"ew".toUByteArray(UTF_8)
        )

        // execute
        val actual: ULong = decode(input)

        // verify
        val expected = 123.toULong()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode ULong with 2 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"MDk".toUByteArray(UTF_8)
        )

        // execute
        val actual: ULong = decode(input)

        // verify
        val expected = 12345.toULong()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode ULong with 4 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"SZYC0g".toUByteArray(UTF_8)
        )

        // execute
        val actual: ULong = decode(input)

        // verify
        val expected = 1234567890.toULong()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode list of strings`() {
        // prepare
        val input = ubyteArrayOf(
            ARRAY_START,
            *"123".toUByteArray(UTF_8),
            ELEMENT_SEPARATOR,
            *"456".toUByteArray(UTF_8),
            ELEMENT_SEPARATOR,
            *"789".toUByteArray(UTF_8),
            CONTAINER_END,
        )

        // execute
        val actual: List<String> = decode(input)

        // verify
        val expected = listOf("123", "456", "789")
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode set of strings`() {
        // prepare
        val input = ubyteArrayOf(
            ARRAY_START,
            *"123".toUByteArray(UTF_8),
            ELEMENT_SEPARATOR,
            *"456".toUByteArray(UTF_8),
            ELEMENT_SEPARATOR,
            *"789".toUByteArray(UTF_8),
            CONTAINER_END,
        )

        // execute
        val actual: Set<String> = decode(input)

        // verify
        val expected = setOf("123", "456", "789")
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode collection of strings`() {
        // prepare
        val input = ubyteArrayOf(
            ARRAY_START,
            *"123".toUByteArray(UTF_8),
            ELEMENT_SEPARATOR,
            *"456".toUByteArray(UTF_8),
            ELEMENT_SEPARATOR,
            *"789".toUByteArray(UTF_8),
            CONTAINER_END,
        )

        // execute
        val actual: Collection<String> = decode(input)

        // verify
        val expected = setOf("123", "456", "789")
        assertThat(actual)
            .containsAll(expected)
            .isInstanceOf(Collection::class.java)
    }

    @Test
    fun `decode map of strings`() {
        // prepare
        val input = ubyteArrayOf(
            OBJECT_START,
            *"map".toUByteArray(UTF_8),
            ELEMENT_SEPARATOR,
            *"123".toUByteArray(UTF_8),
            CONTAINER_END,
        )

        // execute
        val actual: Map<String, String> = decode(input)

        // verify
        val expected = mapOf("map" to "123")
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode map of list of strings`() {
        // prepare
        val input = ubyteArrayOf(
            OBJECT_START,
            *"arr".toUByteArray(UTF_8),
            ELEMENT_SEPARATOR,
            ARRAY_START,
            *"123".toUByteArray(UTF_8),
            ELEMENT_SEPARATOR,
            *"456".toUByteArray(UTF_8),
            CONTAINER_END,
            CONTAINER_END
        )

        // execute
        val actual: Map<String, List<String>> = decode(input)

        // verify
        val expected = mapOf("arr" to listOf("123", "456"))
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode data class`() {
        // prepare
        data class Obj(
            val str: String,
        )

        val input = ubyteArrayOf(
            OBJECT_START,
            *"str".toUByteArray(UTF_8),
            ELEMENT_SEPARATOR,
            *"123".toUByteArray(UTF_8),
            CONTAINER_END
        )

        // execute
        val actual: Obj = decode(input)

        // verify
        val expected = Obj("123")
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode complex data class`() {
        // prepare
        data class Obj(
            val map: Map<String, String>,
            val arr: List<String>
        )

        val input = ubyteArrayOf(
            OBJECT_START,
            *"map".toUByteArray(UTF_8),
            ELEMENT_SEPARATOR,
            OBJECT_START,
            *"456".toUByteArray(UTF_8),
            ELEMENT_SEPARATOR,
            *"789".toUByteArray(UTF_8),
            CONTAINER_END,
            ELEMENT_SEPARATOR,
            *"arr".toUByteArray(UTF_8),
            ELEMENT_SEPARATOR,
            ARRAY_START,
            *"123".toUByteArray(UTF_8),
            ELEMENT_SEPARATOR,
            *"456".toUByteArray(UTF_8),
            CONTAINER_END,
            CONTAINER_END
        )

        // execute
        val actual: Obj = decode(input)

        // verify
        val expected = Obj(
            map = mapOf("456" to "789"),
            arr = listOf("123", "456")
        )
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode array of strings`() {
        // prepare
        val input = ubyteArrayOf(
            ARRAY_START,
            *"123".toUByteArray(UTF_8),
            ELEMENT_SEPARATOR,
            *"456".toUByteArray(UTF_8),
            ELEMENT_SEPARATOR,
            *"789".toUByteArray(UTF_8),
            CONTAINER_END,
        )

        // execute
        val actual: Array<String> = decode(input)

        // verify
        val expected = arrayOf("123", "456", "789")
        assertThat(actual).isEqualTo(expected)
    }

    // todo: add more array tests
}