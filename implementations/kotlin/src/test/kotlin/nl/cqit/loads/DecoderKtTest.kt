@file:OptIn(ExperimentalUnsignedTypes::class)

package nl.cqit.loads

import nl.cqit.loads.model.ARRAY_START
import nl.cqit.loads.model.CONTAINER_END
import nl.cqit.loads.model.ELEMENT_SEPARATOR
import nl.cqit.loads.model.OBJECT_START
import nl.cqit.loads.utils.toUByteArray
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.text.Charsets.UTF_8

class DecoderKtTest {

    @Test
    fun `decode string`() {
        // prepare
        val input = ubyteArrayOf(0x31u, 0x32u, 0x33u)

        // execute
        val actual: String = decode(input)

        // verify
        val expected = "123"
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