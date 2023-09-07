package me.ray.ldapauthenticator.opt

/**
 * 两步验证的账号信息
 * @param secret 密钥
 * @param otpType OPT 类型，只支持 TOPT
 * @param name 名字（解析出来的，只作为展示用）
 * @param interval 每个验证码的有效时间（目前是 30 秒）
 * Interval of time (seconds) between successive changes of this counter's value.
 * @param startTime Earliest time instant (seconds since UNIX epoch) at which this counter assumes the value of
 * {@code 0}.
 *
 */
data class Account(
	val otpType: OtpType = OtpType.TOPT,
	val name: String,
	val secret: String,
	val interval: Long = 30,
	val startTime: Long = 0,
)

sealed interface OtpType {
	object TOPT : OtpType
	object HOPT : OtpType
}