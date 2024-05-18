package fe.linksheet.util

import java.util.*

/*
 * Copyright 2011 Google Inc.
 * Copyright 2018 Andreas Schildbach
 *
 * From https://github.com/bitcoinj/bitcoinj/blob/master/core/src/main/java/org/bitcoinj/core/Base58.java
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * nl.kyllian.utils.Base58 is a way to encode Bitcoin addresses (or arbitrary data) as alphanumeric strings.
 *
 *
 * Note that this is not the same base58 as used by Flickr, which you may find referenced around the Internet.
 *
 *
 * Satoshi explains: why base-58 instead of standard base-64 encoding?
 *
 *  * Don't want 0OIl characters that look the same in some fonts and
 * could be used to create visually identical looking account numbers.
 *  * A string with non-alphanumeric characters is not as easily accepted as an account number.
 *  * E-mail usually won't line-break if there's no punctuation to break at.
 *  * Doubleclicking selects the whole number as one word if it's all alphanumeric.
 *
 *
 *
 * However, note that the encoding/decoding runs in O(n) time, so it is not useful for large data.
 *
 *
 * The basic idea of the encoding is to treat the data bytes as a large number represented using
 * base-256 digits, convert the number to be represented using base-58 digits, preserve the exact
 * number of leading zeros (which are otherwise lost during the mathematical operations on the
 * numbers), and finally represent the resulting base-58 digits as alphanumeric ASCII characters.
 */
object Base58 {
    private val ALPHABET: CharArray = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray()
    private val ENCODED_ZERO = ALPHABET[0]
    private val INDEXES = IntArray(128).apply {
        this.fill(-1)
        ALPHABET.forEachIndexed { index, byte ->
            this[byte.code] = index
        }
    }

    /**
     * Encodes the given bytes as a base58 string (no checksum is appended).
     *
     * @param input the bytes to encode
     * @return the base58-encoded string
     */
    fun encode(input: ByteArray): String {
        var mutInput = input
        if (mutInput.isEmpty()) {
            return ""
        }
        // Count leading zeros.
        var zeros = 0
        while (zeros < mutInput.size && mutInput[zeros].toInt() == 0) {
            ++zeros
        }
        // Convert base-256 digits to base-58 digits (plus conversion to ASCII characters)
        mutInput = mutInput.copyOf(mutInput.size) // since we modify it in-place
        val encoded = CharArray(mutInput.size * 2) // upper bound
        var outputStart = encoded.size
        var inputStart = zeros
        while (inputStart < mutInput.size) {
            encoded[--outputStart] = ALPHABET[divmod(mutInput, inputStart, 256, 58).toInt()]
            if (mutInput[inputStart].toInt() == 0) {
                ++inputStart // optimization - skip leading zeros
            }
        }
        // Preserve exactly as many leading encoded zeros in output as there were leading zeros in input.
        while (outputStart < encoded.size && encoded[outputStart] == ENCODED_ZERO) {
            ++outputStart
        }
        while (--zeros >= 0) {
            encoded[--outputStart] = ENCODED_ZERO
        }
        // Return encoded string (including encoded leading zeros).
        return String(encoded, outputStart, encoded.size - outputStart)
    }

    /**
     * Decodes the given base58 string into the original data bytes.
     *
     * @param input the base58-encoded string to decode
     * @return the decoded data bytes
     */
    fun decode(input: String): ByteArray {
        if (input.isEmpty()) {
            return ByteArray(0)
        }
        // Convert the base58-encoded ASCII chars to a base58 byte sequence (base58 digits).
        val input58 = ByteArray(input.length)
        for (i in input.indices) {
            val c = input[i]
            val digit = if (c.code < 128) INDEXES[c.code] else -1
            check(digit >= 0)
            input58[i] = digit.toByte()
        }

        // Count leading zeros.
        var zeros = 0
        while (zeros < input58.size && input58[zeros].toInt() == 0) {
            ++zeros
        }

        // Convert base-58 digits to base-256 digits.
        val decoded = ByteArray(input.length)
        var outputStart = decoded.size
        var inputStart = zeros
        while (inputStart < input58.size) {
            decoded[--outputStart] = divmod(input58, inputStart, 58, 256)
            if (input58[inputStart].toInt() == 0) {
                ++inputStart // optimization - skip leading zeros
            }
        }
        // Ignore extra leading zeroes that were added during the calculation.
        while (outputStart < decoded.size && decoded[outputStart].toInt() == 0) {
            ++outputStart
        }
        // Return decoded data (including original number of leading zeros).
        return Arrays.copyOfRange(decoded, outputStart - zeros, decoded.size)
    }

    /**
     * Divides a number, represented as an array of bytes each containing a single digit
     * in the specified base, by the given divisor. The given number is modified in-place
     * to contain the quotient, and the return value is the remainder.
     *
     * @param number the number to divide
     * @param firstDigit the index within the array of the first non-zero digit
     * (this is used for optimization by skipping the leading zeros)
     * @param base the base in which the number's digits are represented (up to 256)
     * @param divisor the number to divide by (up to 256)
     * @return the remainder of the division operation
     */
    private fun divmod(number: ByteArray, firstDigit: Int, base: Int, divisor: Int): Byte {
        // this is just long division which accounts for the base of the input digits
        var remainder = 0
        for (i in firstDigit until number.size) {
            val digit = number[i].toInt() and 0xFF
            val temp = remainder * base + digit
            number[i] = (temp / divisor).toByte()
            remainder = temp % divisor
        }
        return remainder.toByte()
    }
}
