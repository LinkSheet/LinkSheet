package fe.linksheet.module.log

import fe.android.preference.helper.PreferenceRepository
import fe.linksheet.extension.decodeHex
import fe.linksheet.module.preference.Preferences
import fe.linksheet.util.CryptoUtil
import org.koin.dsl.module
import kotlin.reflect.KClass

val loggerHmac = CryptoUtil.HmacSha("HmacSHA256", 64)

val loggerFactoryModule = module {
    single {
        val preferenceRepository = get<PreferenceRepository>()
        val logKey = preferenceRepository.getOrWriteInit(Preferences.logKey).decodeHex()

        LoggerFactory(logKey)
    }
}

class LoggerFactory(private val logKey: ByteArray) {
    val logHasher by lazy {
        LogHasher.LogKeyHasher(CryptoUtil.makeHmac(loggerHmac.algorithm, logKey))
    }

    fun createLogger(prefix: KClass<*>) = DefaultLogger(prefix, logHasher)
    fun createLogger(prefix: String) = DefaultLogger(prefix, logHasher)
}