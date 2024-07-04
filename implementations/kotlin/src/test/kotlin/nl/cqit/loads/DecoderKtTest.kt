@file:OptIn(ExperimentalUnsignedTypes::class)

package nl.cqit.loads

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

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
            0x31u, 0x32u, 0x33u,
            0xFFu,
            0x34u, 0x35u, 0x36u,
            0xFFu,
            0x37u, 0x38u, 0x39u,
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
            0x31u, 0x32u, 0x33u,
            0xFFu,
            0x34u, 0x35u, 0x36u,
            0xFFu,
            0x37u, 0x38u, 0x39u,
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
            0x31u, 0x32u, 0x33u,
            0xFFu,
            0x34u, 0x35u, 0x36u,
            0xFFu,
            0x37u, 0x38u, 0x39u,
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
    fun `decode array of strings`() {
        // prepare
        val input = ubyteArrayOf(
            0xFAu,
            0x31u, 0x32u, 0x33u,
            0xFFu,
            0x34u, 0x35u, 0x36u,
            0xFFu,
            0x37u, 0x38u, 0x39u,
            0xFEu,
        )

        // execute
        val actual: Array<String> = decode(input)

        // verify
        val expected = arrayOf("123", "456", "789")
        assertThat(actual).isEqualTo(expected)
    }
}