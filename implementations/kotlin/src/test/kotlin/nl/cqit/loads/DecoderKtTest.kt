@file:OptIn(ExperimentalUnsignedTypes::class)

package nl.cqit.loads

import nl.cqit.loads.model.ARRAY_START
import nl.cqit.loads.model.BINARY_VALUE
import nl.cqit.loads.model.CONTAINER_END
import nl.cqit.loads.model.ELEMENT_SEPARATOR
import nl.cqit.loads.model.NULL_VALUE
import nl.cqit.loads.model.OBJECT_START
import nl.cqit.loads.model.types.ShortType.*
import nl.cqit.loads.utils.toUByteArray
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset.UTC
import java.time.ZonedDateTime
import java.util.stream.Stream
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
    fun `decode String with null value`() {
        // prepare
        val input = ubyteArrayOf(
            NULL_VALUE
        )

        // execute
        val actual: String? = decode(input)

        // verify
        assertThat(actual).isNull()
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
    fun `decode UByteArray with int type`() {
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
    fun `decode UByteArray with null value`() {
        // prepare
        val input = ubyteArrayOf(
            NULL_VALUE
        )

        // execute
        val actual: UByteArray? = decode(input)

        // verify
        assertThat(actual).isNull()
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
    fun `decode ByteArray with null value`() {
        // prepare
        val input = ubyteArrayOf(
            NULL_VALUE
        )

        // execute
        val actual: ByteArray? = decode(input)

        // verify
        assertThat(actual).isNull()
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
    fun `decode Byte with null value`() {
        // prepare
        val input = ubyteArrayOf(
            NULL_VALUE
        )

        // execute
        val actual: Byte? = decode(input)

        // verify
        assertThat(actual).isNull()
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
    fun `decode Short with null value`() {
        // prepare
        val input = ubyteArrayOf(
            NULL_VALUE
        )

        // execute
        val actual: Short? = decode(input)

        // verify
        assertThat(actual).isNull()
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
    fun `decode Int with 3 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"AeJA".toUByteArray(UTF_8)
        )

        // execute
        val actual: Int = decode(input)

        // verify
        val expected = 123456
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode Int with null value`() {
        // prepare
        val input = ubyteArrayOf(
            NULL_VALUE
        )

        // execute
        val actual: Int? = decode(input)

        // verify
        assertThat(actual).isNull()
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
    fun `decode Long with 3 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"AeJA".toUByteArray(UTF_8)
        )

        // execute
        val actual: Long = decode(input)

        // verify
        val expected = 123456L
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
    fun `decode Long with 7 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"BGLVPIq6wA".toUByteArray(UTF_8)
        )

        // execute
        val actual: Long = decode(input)

        // verify
        val expected = 1234567890123456L
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode Long with null value`() {
        // prepare
        val input = ubyteArrayOf(
            NULL_VALUE
        )

        // execute
        val actual: Long? = decode(input)

        // verify
        assertThat(actual).isNull()
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
    fun `decode UByte with null value`() {
        // prepare
        val input = ubyteArrayOf(
            NULL_VALUE
        )

        // execute
        val actual: UByte? = decode(input)

        // verify
        assertThat(actual).isNull()
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
    fun `decode UShort with null value`() {
        // prepare
        val input = ubyteArrayOf(
            NULL_VALUE
        )

        // execute
        val actual: UShort? = decode(input)

        // verify
        assertThat(actual).isNull()
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
        val expected = 1234567890u
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
        val expected = 1234567890u
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
        val expected = 123u
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
        val expected = 12345u
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode UInt with 3 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"AeJA".toUByteArray(UTF_8)
        )

        // execute
        val actual: UInt = decode(input)

        // verify
        val expected = 123456u
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode UInt with null value`() {
        // prepare
        val input = ubyteArrayOf(
            NULL_VALUE
        )

        // execute
        val actual: UInt? = decode(input)

        // verify
        assertThat(actual).isNull()
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
        val expected = 1234567890123456789uL
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
        val expected = 1234567890123456789uL
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
        val expected = 123uL
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
        val expected = 12345uL
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode ULong with 3 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"AeJA".toUByteArray(UTF_8)
        )

        // execute
        val actual: ULong = decode(input)

        // verify
        val expected = 123456uL
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
        val expected = 1234567890uL
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode ULong with 7 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"BGLVPIq6wA".toUByteArray(UTF_8)
        )

        // execute
        val actual: ULong = decode(input)

        // verify
        val expected = 1234567890123456uL
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode ULong with null value`() {
        // prepare
        val input = ubyteArrayOf(
            NULL_VALUE
        )

        // execute
        val actual: ULong? = decode(input)

        // verify
        assertThat(actual).isNull()
    }

    @Test
    fun `decode Float`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"QEkP2w".toUByteArray(UTF_8)
        )

        // execute
        val actual: Float = decode(input)

        // verify
        val expected = 3.1415927f
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode Float with type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *FLOAT.type,
            *"QEkP2w".toUByteArray(UTF_8)
        )

        // execute
        val actual: Float = decode(input)

        // verify
        val expected = 3.1415927f
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode Float with wrong type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *DOUBLE.type,
            *"QEkP2w".toUByteArray(UTF_8)
        )

        // execute and verify
        assertThatIllegalArgumentException()
            .isThrownBy { decode<Float>(input) }
            .withMessage("Expected Float but got DOUBLE")
    }

    @Test
    fun `decode Float with null value`() {
        // prepare
        val input = ubyteArrayOf(
            NULL_VALUE
        )

        // execute
        val actual: Float? = decode(input)

        // verify
        assertThat(actual).isNull()
    }

    @Test
    fun `decode Double`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *"QAkh-1RELRg".toUByteArray(UTF_8)
        )

        // execute
        val actual: Double = decode(input)

        // verify
        val expected = 3.141592653589793
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode Double with type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *DOUBLE.type,
            *"QAkh-1RELRg".toUByteArray(UTF_8)
        )

        // execute
        val actual: Double = decode(input)

        // verify
        val expected = 3.141592653589793
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode Double with wrong type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *FLOAT.type,
            *"QAkh-1RELRg".toUByteArray(UTF_8)
        )

        // execute and verify
        assertThatIllegalArgumentException()
            .isThrownBy { decode<Double>(input) }
            .withMessage("Expected Double but got FLOAT")
    }

    @Test
    fun `decode Double with null value`() {
        // prepare
        val input = ubyteArrayOf(
            NULL_VALUE
        )

        // execute
        val actual: Double? = decode(input)

        // verify
        assertThat(actual).isNull()
    }

    @Test
    fun `decode Boolean with 'true' type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *TRUE.type
        )

        // execute
        val actual: Boolean = decode(input)

        // verify
        assertThat(actual).isTrue()
    }

    @Test
    fun `decode Boolean with 'false' type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *FALSE.type
        )

        // execute
        val actual: Boolean = decode(input)

        // verify
        assertThat(actual).isFalse()
    }

    @ParameterizedTest
    @CsvSource(
        "B, true",  // base64 encoded 1
        "A, false", // base64 encoded 0
        "C, true",  // base64 encoded 2
        "-, true",  // base64 encoded 62
        "_, true",  // base64 encoded 63
        "1, true",  // unencoded 1
        "0, false", // unencoded 0
        "T, true",  // shorthand for true
        "t, true",  // shorthand for true
        "F, false", // shorthand for false
        "f, false", // shorthand for false
    )
    fun `decode Boolean with single-boolean type`(value: String, expected: Boolean) {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *BOOLEAN1.type,
            *value.toUByteArray(UTF_8)
        )

        // execute
        val actual: Boolean = decode(input)

        // verify
        assertThat(actual).isEqualTo(expected)
    }

    companion object {
        @JvmStatic
        fun `decode Boolean with multiple-boolean types`(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("!1", "A", arrayOf(false)),
                Arguments.of("!1", "B", arrayOf(true)),
                Arguments.of("!2",  "B", arrayOf(true, false)),
                Arguments.of("!2",  "C", arrayOf(false, true)),
                Arguments.of("!3",  "D", arrayOf(true, true, false)),
                Arguments.of("!3",  "E", arrayOf(false, false, true)),
                Arguments.of("!4",  "H", arrayOf(true, true, true, false)),
                Arguments.of("!4",  "I", arrayOf(false, false, false, true)),
                Arguments.of("!5",  "P", arrayOf(true, true, true, true, false)),
                Arguments.of("!5",  "Q", arrayOf(false, false, false, false, true)),
                Arguments.of("!6",  "f", arrayOf(true, true, true, true, true, false)),
                Arguments.of("!6",  "g", arrayOf(false, false, false, false, false, true)),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("decode Boolean with multiple-boolean types")
    fun `decode Boolean with multiple-boolean types`(type: String, value: String, expected: Array<Boolean>) {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *type.toUByteArray(UTF_8),
            *value.toUByteArray(UTF_8)
        )

        // execute
        val actual: Array<Boolean> = decode(input)

        // verify
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode Boolean with wrong type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *INT.type
        )

        // execute and verify
        assertThatIllegalArgumentException()
            .isThrownBy { decode<Boolean>(input) }
            .withMessage("Expected Boolean but got INT")
    }

    @Test
    fun `decode Boolean with null value`() {
        // prepare
        val input = ubyteArrayOf(
            NULL_VALUE
        )

        // execute
        val actual: Boolean? = decode(input)

        // verify
        assertThat(actual).isNull()
    }

    @Test
    fun `decode Instant with 1 byte`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *TIMESTAMP4.type,
            *"ew".toUByteArray(UTF_8)
        )

        // execute
        val actual: Instant = decode(input)

        // verify
        val expected = Instant.ofEpochSecond(123)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode Instant with 2 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *TIMESTAMP4.type,
            *"MDk".toUByteArray(UTF_8)
        )

        // execute
        val actual: Instant = decode(input)

        // verify
        val expected = Instant.ofEpochSecond(12345)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode Instant with 3 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *TIMESTAMP4.type,
            *"AeJA".toUByteArray(UTF_8)
        )

        // execute
        val actual: Instant = decode(input)

        // verify
        val expected = Instant.ofEpochSecond(123456)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode Instant with 4 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *TIMESTAMP4.type,
            *"SZYC0g".toUByteArray(UTF_8)
        )

        // execute
        val actual: Instant = decode(input)

        // verify
        val expected = Instant.ofEpochSecond(1234567890)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode Instant with 7 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *TIMESTAMP8.type,
            *"BGLVPIq6wA".toUByteArray(UTF_8)
        )

        // execute
        val actual: Instant = decode(input)

        // verify
        val expected = Instant.ofEpochMilli(1234567890123456)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode Instant with 8 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *TIMESTAMP8.type,
            *"ESIQ9H3pgRU".toUByteArray(UTF_8)
        )

        // execute
        val actual: Instant = decode(input)

        // verify
        val expected = Instant.ofEpochMilli(1234567890123456789)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode Instant with 8 bytes as @C`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *TIMESTAMP12.type,
            *"B1vNFQdbzRU".toUByteArray(UTF_8)
        )

        // execute
        val actual: Instant = decode(input)

        // verify
        val expected = Instant.ofEpochSecond(123456789, 123456789)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode Instant with 12 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *TIMESTAMP12.type,
            *"K9xUXWtLhwdbzRU".toUByteArray(UTF_8)
        )

        // execute
        val actual: Instant = decode(input)

        // verify
        val expected = Instant.ofEpochSecond(12345678901234567, 123456789)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode Instant with wrong type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *INT.type,
            *"SZYC0g".toUByteArray(UTF_8)
        )

        // execute and verify
        assertThatIllegalArgumentException()
            .isThrownBy { decode<Instant>(input) }
            .withMessage("Expected Instant but got INT")
    }

    @Test
    fun `decode Instant with null value`() {
        // prepare
        val input = ubyteArrayOf(
            NULL_VALUE
        )

        // execute
        val actual: Instant? = decode(input)

        // verify
        assertThat(actual).isNull()
    }

    @Test
    fun `decode OffsetDateTime with 1 byte`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *TIMESTAMP4.type,
            *"ew".toUByteArray(UTF_8)
        )

        // execute
        val actual: OffsetDateTime = decode(input)

        // verify
        val expected = Instant.ofEpochSecond(123).atOffset(UTC)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode OffsetDateTime with 2 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *TIMESTAMP4.type,
            *"MDk".toUByteArray(UTF_8)
        )

        // execute
        val actual: OffsetDateTime = decode(input)

        // verify
        val expected = Instant.ofEpochSecond(12345).atOffset(UTC)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode OffsetDateTime with 3 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *TIMESTAMP4.type,
            *"AeJA".toUByteArray(UTF_8)
        )

        // execute
        val actual: OffsetDateTime = decode(input)

        // verify
        val expected = Instant.ofEpochSecond(123456).atOffset(UTC)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode OffsetDateTime with 4 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *TIMESTAMP4.type,
            *"SZYC0g".toUByteArray(UTF_8)
        )

        // execute
        val actual: OffsetDateTime = decode(input)

        // verify
        val expected = Instant.ofEpochSecond(1234567890).atOffset(UTC)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode OffsetDateTime with 7 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *TIMESTAMP8.type,
            *"BGLVPIq6wA".toUByteArray(UTF_8)
        )

        // execute
        val actual: OffsetDateTime = decode(input)

        // verify
        val expected = Instant.ofEpochMilli(1234567890123456).atOffset(UTC)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode OffsetDateTime with 8 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *TIMESTAMP8.type,
            *"ESIQ9H3pgRU".toUByteArray(UTF_8)
        )

        // execute
        val actual: OffsetDateTime = decode(input)

        // verify
        val expected = Instant.ofEpochMilli(1234567890123456789).atOffset(UTC)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode OffsetDateTime with 8 bytes as @C`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *TIMESTAMP12.type,
            *"B1vNFQdbzRU".toUByteArray(UTF_8)
        )

        // execute
        val actual: OffsetDateTime = decode(input)

        // verify
        val expected = Instant.ofEpochSecond(123456789, 123456789).atOffset(UTC)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode OffsetDateTime with 12 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *TIMESTAMP12.type,
            *"K9xUXWtLhwdbzRU".toUByteArray(UTF_8)
        )

        // execute
        val actual: OffsetDateTime = decode(input)

        // verify
        val expected = Instant.ofEpochSecond(12345678901234567, 123456789).atOffset(UTC)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode OffsetDateTime with wrong type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *INT.type,
            *"SZYC0g".toUByteArray(UTF_8)
        )

        // execute and verify
        assertThatIllegalArgumentException()
            .isThrownBy { decode<OffsetDateTime>(input) }
            .withMessage("Expected OffsetDateTime but got INT")
    }

    @Test
    fun `decode OffsetDateTime with null value`() {
        // prepare
        val input = ubyteArrayOf(
            NULL_VALUE
        )

        // execute
        val actual: OffsetDateTime? = decode(input)

        // verify
        assertThat(actual).isNull()
    }

    @Test
    fun `decode ZonedDateTime with 1 byte`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *TIMESTAMP4.type,
            *"ew".toUByteArray(UTF_8)
        )

        // execute
        val actual: ZonedDateTime = decode(input)

        // verify
        val expected = Instant.ofEpochSecond(123).atZone(UTC)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode ZonedDateTime with 2 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *TIMESTAMP4.type,
            *"MDk".toUByteArray(UTF_8)
        )

        // execute
        val actual: ZonedDateTime = decode(input)

        // verify
        val expected = Instant.ofEpochSecond(12345).atZone(UTC)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode ZonedDateTime with 3 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *TIMESTAMP4.type,
            *"AeJA".toUByteArray(UTF_8)
        )

        // execute
        val actual: ZonedDateTime = decode(input)

        // verify
        val expected = Instant.ofEpochSecond(123456).atZone(UTC)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode ZonedDateTime with 4 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *TIMESTAMP4.type,
            *"SZYC0g".toUByteArray(UTF_8)
        )

        // execute
        val actual: ZonedDateTime = decode(input)

        // verify
        val expected = Instant.ofEpochSecond(1234567890).atZone(UTC)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode ZonedDateTime with 7 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *TIMESTAMP8.type,
            *"BGLVPIq6wA".toUByteArray(UTF_8)
        )

        // execute
        val actual: ZonedDateTime = decode(input)

        // verify
        val expected = Instant.ofEpochMilli(1234567890123456).atZone(UTC)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode ZonedDateTime with 8 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *TIMESTAMP8.type,
            *"ESIQ9H3pgRU".toUByteArray(UTF_8)
        )

        // execute
        val actual: ZonedDateTime = decode(input)

        // verify
        val expected = Instant.ofEpochMilli(1234567890123456789).atZone(UTC)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode ZonedDateTime with 8 bytes as @C`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *TIMESTAMP12.type,
            *"B1vNFQdbzRU".toUByteArray(UTF_8)
        )

        // execute
        val actual: ZonedDateTime = decode(input)

        // verify
        val expected = Instant.ofEpochSecond(123456789, 123456789).atZone(UTC)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode ZonedDateTime with 12 bytes`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *TIMESTAMP12.type,
            *"K9xUXWtLhwdbzRU".toUByteArray(UTF_8)
        )

        // execute
        val actual: ZonedDateTime = decode(input)

        // verify
        val expected = Instant.ofEpochSecond(12345678901234567, 123456789).atZone(UTC)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode ZonedDateTime with wrong type`() {
        // prepare
        val input = ubyteArrayOf(
            BINARY_VALUE,
            *INT.type,
            *"SZYC0g".toUByteArray(UTF_8)
        )

        // execute and verify
        assertThatIllegalArgumentException()
            .isThrownBy { decode<ZonedDateTime>(input) }
            .withMessage("Expected ZonedDateTime but got INT")
    }

    @Test
    fun `decode ZonedDateTime with null value`() {
        // prepare
        val input = ubyteArrayOf(
            NULL_VALUE
        )

        // execute
        val actual: ZonedDateTime? = decode(input)

        // verify
        assertThat(actual).isNull()
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