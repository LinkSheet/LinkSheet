package fe.linksheet.module.log

import android.app.Application
import android.util.Log
import fe.android.preference.helper.PreferenceRepository
import fe.linksheet.LinkSheetApp
import fe.linksheet.extension.decodeHex
import fe.linksheet.module.log.LogDumpable.Companion.dumpObject
import fe.linksheet.module.preference.Preferences
import fe.linksheet.util.CryptoUtil
import org.koin.dsl.module
import javax.crypto.Mac
import kotlin.reflect.KClass

val loggerHmac = CryptoUtil.HmacSha("HmacSHA256", 64)

val loggerFactoryModule = module {
    single {
        val preferenceRepository = get<PreferenceRepository>()
        val logKey = preferenceRepository.getOrWriteInit(Preferences.logKey).decodeHex()

        LoggerFactory(get<Application>() as LinkSheetApp, logKey)
    }
}

class LoggerFactory(private val context: LinkSheetApp, private val logKey: ByteArray) {
    private val mac by lazy {
        CryptoUtil.makeHmac(loggerHmac.algorithm, logKey)
    }

    fun createLogger(prefix: KClass<*>) = Logger(context, prefix, mac)
    fun createLogger(prefix: String) = Logger(context, prefix, mac)
}