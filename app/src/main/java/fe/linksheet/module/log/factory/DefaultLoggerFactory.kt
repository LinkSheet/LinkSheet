package fe.linksheet.module.log.factory

import fe.kotlin.extension.string.decodeHexOrThrow
import fe.linksheet.module.log.file.FileAppLogger
import fe.linksheet.module.log.impl.hasher.LogHasher
import fe.linksheet.module.log.impl.DebugLogger
import fe.linksheet.module.log.impl.DefaultLogger
import fe.linksheet.module.log.impl.Logger
import fe.linksheet.module.preference.AppPreferenceRepository
import fe.linksheet.module.preference.AppPreferences
import fe.linksheet.util.CryptoUtil
import org.koin.dsl.module
import kotlin.reflect.KClass


val defaultLoggerFactoryModule = module {
    single<LoggerFactory> {
        val preferenceRepository = get<AppPreferenceRepository>()
        val logKey = preferenceRepository.getOrWriteInit(AppPreferences.logKey).decodeHexOrThrow()

        DefaultLoggerFactory(logKey, get())
    }
}

interface LoggerFactory {
    fun createLogger(prefix: KClass<*>): Logger
    fun createLogger(prefix: String): Logger

    companion object {
        val hmac = CryptoUtil.HmacSha("HmacSHA256", 64)
    }
}

class DebugLoggerFactory(private val fileAppLogger: FileAppLogger) : LoggerFactory {
    override fun createLogger(prefix: KClass<*>) = DebugLogger(prefix, fileAppLogger)
    override fun createLogger(prefix: String) = DebugLogger(prefix, fileAppLogger)

    companion object {
        val module = module { single<LoggerFactory> { DebugLoggerFactory(get()) } }
    }
}

class DefaultLoggerFactory(private val logKey: ByteArray, private val fileAppLogger: FileAppLogger) : LoggerFactory {
    private val logHasher by lazy {
        LogHasher.LogKeyHasher(CryptoUtil.makeHmac(LoggerFactory.hmac.algorithm, logKey))
    }

    override fun createLogger(prefix: KClass<*>) = DefaultLogger(prefix, logHasher, fileAppLogger)
    override fun createLogger(prefix: String) = DefaultLogger(prefix, logHasher, fileAppLogger)
}
