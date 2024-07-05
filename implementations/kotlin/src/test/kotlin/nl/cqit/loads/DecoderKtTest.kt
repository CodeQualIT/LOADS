@file:OptIn(ExperimentalUnsignedTypes::class)

package nl.cqit.loads

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
            0xFAu,
            *"123".toUByteArray(UTF_8),
            0xFFu,
            *"456".toUByteArray(UTF_8),
            0xFFu,
            *"789".toUByteArray(UTF_8),
            0xFEu,
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
            0xFAu,
            *"123".toUByteArray(UTF_8),
            0xFFu,
            *"456".toUByteArray(UTF_8),
            0xFFu,
            *"789".toUByteArray(UTF_8),
            0xFEu,
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
            0xFAu,
            *"123".toUByteArray(UTF_8),
            0xFFu,
            *"456".toUByteArray(UTF_8),
            0xFFu,
            *"789".toUByteArray(UTF_8),
            0xFEu,
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
            0xFCu,
            *"map".toUByteArray(UTF_8),
            0xFFu,
            *"123".toUByteArray(UTF_8),
            0xFEu,
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
            0xFCu,
            *"arr".toUByteArray(UTF_8),
            0xFFu,
            0xFAu,
            *"123".toUByteArray(UTF_8),
            0xFFu,
            *"456".toUByteArray(UTF_8),
            0xFEu,
            0xFEu
        )

        // execute
        val actual: Map<String, List<String>> = decode(input)

        // verify
        val expected = mapOf("arr" to listOf("123", "456"))
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `decode array of strings`() {
        // prepare
        val input = ubyteArrayOf(
            0xFAu,
            *"123".toUByteArray(UTF_8),
            0xFFu,
            *"456".toUByteArray(UTF_8),
            0xFFu,
            *"789".toUByteArray(UTF_8),
            0xFEu,
        )

        // execute
        val actual: Array<String> = decode(input)

        // verify
        val expected = arrayOf("123", "456", "789")
        assertThat(actual).isEqualTo(expected)
    }
}