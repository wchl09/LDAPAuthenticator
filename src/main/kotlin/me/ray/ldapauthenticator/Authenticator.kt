package me.ray.ldapauthenticator

import me.ray.ldapauthenticator.opt.Account
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.nio.ByteBuffer
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * 目前支持TOPT，8 位验证码 ， 30 秒刷新一次
 */
object Authenticator {
	private val DIGITS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".toCharArray()
	private val MASK = DIGITS.size - 1
	private val SHIFT = DIGITS.size.countTrailingZeroBits()
	private val CHAR_MAP = mapOf(*DIGITS.mapIndexed { index, c -> c to index }.toTypedArray())
	private val DIGITS_POWER =
		intArrayOf(1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000)

	private fun Account.getCode(): Long = (System.currentTimeMillis() / 1000 - this.startTime).run {
		takeIf { this >= 0 }?.let { it / this@getCode.interval }
			?: ((this - (this@getCode.interval - 1)) / this@getCode.interval)
	}

	fun Account.computePin(): String {
		val code = getCode()
		val hash = runCatching {
			val keyBytes = secret.base32decode()
			val mac = Mac.getInstance("HMACSHA1")
			mac.init(SecretKeySpec(keyBytes, ""))
			val value = ByteBuffer.allocate(8).putLong(code).array();
			return@runCatching mac.doFinal(value)
		}.getOrThrow()
		val offset = hash.last().toInt().and(0xF)
		val result = DataInputStream(ByteArrayInputStream(hash, offset, hash.size - offset))
			.use {
				(it.readInt().and(0x7FFFFFFF) % DIGITS_POWER[6]).toString()
			}
		return result.takeIf { it.length == 6 } ?: buildString {
			repeat(6 - result.length) {
				append("0")
			}
			append(result)
		}
	}

	private fun String.base32decode(): ByteArray {
		val encoded = trim()
			.replace(" ", "")
			.replace("-", "")
			.replaceFirst("[=]*$", "")
			.uppercase(Locale.US)
		if (encoded.isEmpty()) return ByteArray(0)
		val encodedLength = encoded.length
		val outLength = encodedLength * SHIFT / 8
		val result = ByteArray(outLength)
		var buffer = 0
		var next = 0
		var bitsLeft = 0
		encoded.toCharArray().forEach {
			check(CHAR_MAP.containsKey(it)) { "" }
			buffer = buffer.shl(SHIFT)
			buffer = buffer.or(CHAR_MAP.getValue(it).and(MASK))
			bitsLeft += SHIFT
			if (bitsLeft >= 8) {
				result[next++] = buffer.shr(bitsLeft - 8).toByte()
				bitsLeft -= 8
			}
		}
		return result
	}
}