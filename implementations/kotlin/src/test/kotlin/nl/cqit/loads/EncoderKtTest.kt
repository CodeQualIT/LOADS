@file:OptIn(ExperimentalUnsignedTypes::class)

package nl.cqit.loads

import nl.cqit.loads.model.ARRAY_START
import nl.cqit.loads.model.BINARY_VALUE
import nl.cqit.loads.model.CONTAINER_END
import nl.cqit.loads.model.ELEMENT_SEPARATOR
import nl.cqit.loads.model.NULL_VALUE
import nl.cqit.loads.model.OBJECT_START
import nl.cqit.loads.utils.toUByteArray
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.ZoneOffset
import kotlin.text.Charsets.UTF_8

class EncoderKtTest {

    @Test
    fun `Encoding a null value`() {
        // prepare
        val obj = null

        // execute
        val result1 = from(obj)
        val result2 = obj.toLoads()

        // verify
        val expected = ubyteArrayOf(
            NULL_VALUE
        )
        assertThat(result1).containsSequence(expected)
        assertThat(result2).containsSequence(expected)
    }

    @Test
    fun `Encoding an empty string`() {
        // prepare
        val obj = ""

        // execute
        val result1 = from(obj)
        val result2 = obj.toLoads()

        // verify
        val expected = ubyteArrayOf()
        assertThat(result1).containsSequence(expected)
        assertThat(result2).containsSequence(expected)
    }

    @Test
    fun `Encoding a string`() {
        // prepare
        val obj = "Hello, World!"

        // execute
        val result1 = from(obj)
        val result2 = obj.toLoads()

        // verify
        val expected = ubyteArrayOf(
            // Hello,
            0x48u, 0x65u, 0x6Cu, 0x6Cu, 0x6Fu, 0x2Cu,
            // <space>
            0x20u,
            // World!
            0x57u, 0x6Fu, 0x72u, 0x6Cu, 0x64u, 0x21u
        )
        assertThat(result1).containsSequence(expected)
        assertThat(result2).containsSequence(expected)
    }

    @Test
    fun `Encoding a string with an emoji`() {
        // prepare
        val obj = "Hello 🌎!"

        // execute
        val result1 = from(obj)
        val result2 = obj.toLoads()

        // verify
        val expected = ubyteArrayOf(
            // Hello
            0x48u, 0x65u, 0x6Cu, 0x6Cu, 0x6Fu,
            // <space>
            0x20u,
            // 🌎
            0xF0u, 0x9Fu, 0x8Cu, 0x8Eu,
            // !
            0x21u
        )
        assertThat(result1).containsSequence(expected)
        assertThat(result2).containsSequence(expected)
    }

    @Test
    fun `Encoding an array of strings`() {
        // prepare
        val obj = arrayOf("Hello", "World", "🌎")

        // execute
        val result1 = from(obj)
        val result2 = obj.toLoads()

        // verify
        val expected = ubyteArrayOf(
            ARRAY_START,
            *"Hello".toUByteArray(UTF_8),
            ELEMENT_SEPARATOR,
            *"World".toUByteArray(UTF_8),
            ELEMENT_SEPARATOR,
            *"🌎".toUByteArray(UTF_8),
            CONTAINER_END
        )
        assertThat(result1).containsSequence(expected)
        assertThat(result2).containsSequence(expected)
    }

    @Test
    fun `Encoding a data class`() {
        // prepare
        data class Data(val firstName: String, val lastName: String)

        val obj = Data("John", "Doe")

        // execute
        val result1 = from(obj)
        val result2 = obj.toLoads()

        // verify
        val expected = ubyteArrayOf(
            OBJECT_START,
            *"firstName".toUByteArray(UTF_8),
            ELEMENT_SEPARATOR,
            *"John".toUByteArray(UTF_8),
            ELEMENT_SEPARATOR,
            *"lastName".toUByteArray(UTF_8),
            ELEMENT_SEPARATOR,
            *"Doe".toUByteArray(UTF_8),
            CONTAINER_END
        )
        assertThat(result1).containsSequence(expected)
        assertThat(result2).containsSequence(expected)
    }

    @Test
    fun `Encoding a data class with a nested data class with fields alphabetically`() {
        // prepare
        data class Address(val street: String, val city: String)
        data class Person(val name: String, val address: Address)

        val obj = Person("John", Address("Main Street", "New York"))

        // execute
        val result1 = from(obj)
        val result2 = obj.toLoads()

        // verify
        val expected = ubyteArrayOf(
            OBJECT_START,
              *"address".toUByteArray(UTF_8),
              ELEMENT_SEPARATOR,
              OBJECT_START,
                *"city".toUByteArray(UTF_8),
                ELEMENT_SEPARATOR,
                *"New York".toUByteArray(UTF_8),
                ELEMENT_SEPARATOR,
                *"street".toUByteArray(UTF_8),
                ELEMENT_SEPARATOR,
                *"Main Street".toUByteArray(UTF_8),
              CONTAINER_END,
              ELEMENT_SEPARATOR,
              *"name".toUByteArray(UTF_8),
              ELEMENT_SEPARATOR,
              *"John".toUByteArray(UTF_8),
            CONTAINER_END
        )
        assertThat(result1).containsSequence(expected)
        assertThat(result2).containsSequence(expected)
    }

    @Test
    fun `Encoding a byte`() {
        // prepare
        val obj = 123.toByte()

        // execute
        val result1 = from(obj)
        val result2 = obj.toLoads()

        // verify
        val expected = ubyteArrayOf(
            BINARY_VALUE,
            *"#1".toUByteArray(UTF_8),
            *"ew".toUByteArray(UTF_8),
        )
        assertThat(result1).containsSequence(expected)
        assertThat(result2).containsSequence(expected)
    }

    @Test
    fun `Encoding a short`() {
        // prepare
        val obj = 12345.toShort()

        // execute
        val result1 = from(obj)
        val result2 = obj.toLoads()

        // verify
        val expected = ubyteArrayOf(
            BINARY_VALUE,
            *"#2".toUByteArray(UTF_8),
            *"MDk".toUByteArray(UTF_8),
        )
        assertThat(result1).containsSequence(expected)
        assertThat(result2).containsSequence(expected)
    }

    @Test
    fun `Encoding an integer`() {
        // prepare
        val obj = 1234567890

        // execute
        val result1 = from(obj)
        val result2 = obj.toLoads()

        // verify
        val expected = ubyteArrayOf(
            BINARY_VALUE,
            *"#4".toUByteArray(UTF_8),
            *"SZYC0g".toUByteArray(UTF_8),
        )
        assertThat(result1).containsSequence(expected)
        assertThat(result2).containsSequence(expected)
    }

    @Test
    fun `Encoding a long`() {
        // prepare
        val obj = 1234567890123456789L

        // execute
        val result1 = from(obj)
        val result2 = obj.toLoads()

        // verify
        val expected = ubyteArrayOf(
            BINARY_VALUE,
            *"#8".toUByteArray(UTF_8),
            *"ESIQ9H3pgRU".toUByteArray(UTF_8),
        )
        assertThat(result1).containsSequence(expected)
        assertThat(result2).containsSequence(expected)
    }

    @Test
    fun `Encoding an unsigned byte`() {
        // prepare
        val obj = 123u.toUByte()

        // execute
        val result1 = from(obj)
        val result2 = obj.toLoads()

        // verify
        val expected = ubyteArrayOf(
            BINARY_VALUE,
            *"+1".toUByteArray(UTF_8),
            *"ew".toUByteArray(UTF_8),
        )
        assertThat(result1).containsSequence(expected)
        assertThat(result2).containsSequence(expected)
    }

    @Test
    fun `Encoding an unsigned byte over 128`() {
        // prepare
        val obj = 129u.toUByte()

        // execute
        val result1 = from(obj)
        val result2 = obj.toLoads()

        // verify
        val expected = ubyteArrayOf(
            BINARY_VALUE,
            *"+1".toUByteArray(UTF_8),
            *"gQ".toUByteArray(UTF_8),
        )
        assertThat(result1).containsSequence(expected)
        assertThat(result2).containsSequence(expected)
    }

    @Test
    fun `Encoding an unsigned short`() {
        // prepare
        val obj = 12345u.toUShort()

        // execute
        val result1 = from(obj)
        val result2 = obj.toLoads()

        // verify
        val expected = ubyteArrayOf(
            BINARY_VALUE,
            *"+2".toUByteArray(UTF_8),
            *"MDk".toUByteArray(UTF_8),
        )
        assertThat(result1).containsSequence(expected)
        assertThat(result2).containsSequence(expected)
    }

    @Test
    fun `Encoding an unsigned integer`() {
        // prepare
        val obj = 1234567890u

        // execute
        val result1 = from(obj)
        val result2 = obj.toLoads()

        // verify
        val expected = ubyteArrayOf(
            BINARY_VALUE,
            *"+4".toUByteArray(UTF_8),
            *"SZYC0g".toUByteArray(UTF_8),
        )
        assertThat(result1).containsSequence(expected)
        assertThat(result2).containsSequence(expected)
    }

    @Test
    fun `Encoding an unsigned long`() {
        // prepare
        val obj = 1234567890123456789uL

        // execute
        val result1 = from(obj)
        val result2 = obj.toLoads()

        // verify
        val expected = ubyteArrayOf(
            BINARY_VALUE,
            *"+8".toUByteArray(UTF_8),
            *"ESIQ9H3pgRU".toUByteArray(UTF_8),
        )
        assertThat(result1).containsSequence(expected)
        assertThat(result2).containsSequence(expected)
    }

    @Test
    fun `Encoding a float`() {
        // prepare
        val obj = 3.1415927f

        // execute
        val result1 = from(obj)
        val result2 = obj.toLoads()

        // verify
        val expected = ubyteArrayOf(
            BINARY_VALUE,
            *"~4".toUByteArray(UTF_8),
            *"QEkP2w".toUByteArray(UTF_8),
        )
        assertThat(result1).containsSequence(expected)
        assertThat(result2).containsSequence(expected)
    }

    @Test
    fun `Encoding a double`() {
        // prepare
        val obj = 3.141592653589793

        // execute
        val result1 = from(obj)
        val result2 = obj.toLoads()

        // verify
        val expected = ubyteArrayOf(
            BINARY_VALUE,
            *"~8".toUByteArray(UTF_8),
            *"QAkh-1RELRg".toUByteArray(UTF_8),
        )
        assertThat(result1).containsSequence(expected)
        assertThat(result2).containsSequence(expected)
    }

    @Test
    fun `Encoding a true boolean`() {
        // prepare
        val obj = true

        // execute
        val result1 = from(obj)
        val result2 = obj.toLoads()

        // verify
        val expected = ubyteArrayOf(
            BINARY_VALUE,
            *"!t".toUByteArray(UTF_8),
        )
        assertThat(result1).containsSequence(expected)
        assertThat(result2).containsSequence(expected)
    }

    @Test
    fun `Encoding a false boolean`() {
        // prepare
        val obj = false

        // execute
        val result1 = from(obj)
        val result2 = obj.toLoads()

        // verify
        val expected = ubyteArrayOf(
            BINARY_VALUE,
            *"!f".toUByteArray(UTF_8),
        )
        assertThat(result1).containsSequence(expected)
        assertThat(result2).containsSequence(expected)
    }

    @Test
    fun `Encoding an object with a toByteArray method`() {
        // prepare
        class Test {
            fun toByteArray() = byteArrayOf(0x01, 0x02, 0x03)
        }

        val obj = Test()

        // execute
        val result1 = from(obj)
        val result2 = obj.toLoads()

        // verify
        val expected = ubyteArrayOf(
            BINARY_VALUE,
            *"(Test)".toUByteArray(UTF_8),
            *"AQID".toUByteArray(UTF_8),
        )
        assertThat(result1).containsSequence(expected)
        assertThat(result2).containsSequence(expected)
    }

    @Test
    fun `Encoding an object with a toUByteArray method`() {
        // prepare
        class Test {
            fun toUByteArray() = ubyteArrayOf(0x01u, 0x02u, 0x03u)
        }

        val obj = Test()

        // execute
        val result1 = from(obj)
        val result2 = obj.toLoads()

        // verify
        val expected = ubyteArrayOf(
            BINARY_VALUE,
            *"(Test)".toUByteArray(UTF_8),
            *"AQID".toUByteArray(UTF_8),
        )
        assertThat(result1).containsSequence(expected)
        assertThat(result2).containsSequence(expected)
    }

    @Test
    fun `Encoding a ByteArray object`() {
        // prepare
        val obj = byteArrayOf(0x01, 0x02, 0x03)

        // execute
        val result1 = fromByteArray("test", obj)
        val result2 = obj.toLoads("test")

        // verify
        val expected = ubyteArrayOf(
            BINARY_VALUE,
            *"(test)".toUByteArray(UTF_8),
            *"AQID".toUByteArray(UTF_8),
        )
        assertThat(result1).containsSequence(expected)
        assertThat(result2).containsSequence(expected)
    }

    @Test
    fun `Encoding a UByteArray object`() {
        // prepare
        val obj = ubyteArrayOf(0x01u, 0x02u, 0x03u)

        // execute
        val result1 = fromUByteArray("test", obj)
        val result2 = obj.toLoads("test")

        // verify
        val expected = ubyteArrayOf(
            BINARY_VALUE,
            *"(test)".toUByteArray(UTF_8),
            *"AQID".toUByteArray(UTF_8),
        )
        assertThat(result1).containsSequence(expected)
        assertThat(result2).containsSequence(expected)
    }

    @Test
    fun `Encoding an Instant object`() {
        // prepare
        val obj = Instant.ofEpochSecond(1718315521, 191598900)

        // execute
        val result1 = from(obj)
        val result2 = obj.toLoads()

        // verify
        val expected = ubyteArrayOf(
            BINARY_VALUE,
            *"@C".toUByteArray(UTF_8),
            *"ZmtqAQtrkTQ".toUByteArray(UTF_8),
        )
        assertThat(result1).containsSequence(expected)
        assertThat(result2).containsSequence(expected)
    }

    @Test
    fun `Encoding an OffsetDateTime object`() {
        // prepare
        val obj = Instant.ofEpochSecond(1718315521, 191598900)
            .atOffset(ZoneOffset.UTC)

        // execute
        val result1 = from(obj)
        val result2 = obj.toLoads()

        // verify
        val expected = ubyteArrayOf(
            BINARY_VALUE,
            *"@C".toUByteArray(UTF_8),
            *"ZmtqAQtrkTQ".toUByteArray(UTF_8),
        )
        assertThat(result1).containsSequence(expected)
        assertThat(result2).containsSequence(expected)
    }

    @Test
    fun `Encoding a ZonedDateTime object`() {
        // prepare
        val obj = Instant.ofEpochSecond(1718315521, 191598900)
            .atZone(ZoneOffset.UTC)

        // execute
        val result1 = from(obj)
        val result2 = obj.toLoads()

        // verify
        val expected = ubyteArrayOf(
            BINARY_VALUE,
            *"@C".toUByteArray(UTF_8),
            *"ZmtqAQtrkTQ".toUByteArray(UTF_8),
        )
        assertThat(result1).containsSequence(expected)
        assertThat(result2).containsSequence(expected)
    }
}