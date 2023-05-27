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
import java.time.LocalDateTime
import javax.crypto.Mac
import kotlin.reflect.KClass

val loggerHmac = CryptoUtil.HmacSha("HmacSHA256", 64)

val loggerFactoryModule = module {
    single {
        val preferenceRepository = get<PreferenceRepository>()
        val logKey = preferenceRepository.getOrWriteInit(Preferences.logKey).decodeHex()

        val app = get<Application>()
        Log.d("Lel", "$app")


        LoggerFactory(app as LinkSheetApp, logKey)
    }
}

class LoggerFactory(private val context: LinkSheetApp, private val logKey: ByteArray) {
    private val mac by lazy {
        CryptoUtil.makeHmac(loggerHmac.algorithm, logKey)
    }

    fun createLogger(prefix: KClass<*>) = Logger(context, prefix, mac)
    fun createLogger(prefix: String) = Logger(context, prefix, mac)
}

class Logger(private val linkSheetApp: LinkSheetApp, private val prefix: String, mac: Mac) {
    constructor(
        context: LinkSheetApp,
        clazz: KClass<*>,
        mac: Mac
    ) : this(context, clazz.simpleName!!, mac)

    private val logHasher = LogHasher.LogKeyHasher(mac)

    enum class Type {
        Verbose {
            override fun print(tag: String, msg: String): Int = Log.v(tag, msg)
        },
        Info {
            override fun print(tag: String, msg: String): Int = Log.i(tag, msg)
        },
        Debug {
            override fun print(tag: String, msg: String): Int = Log.d(tag, msg)
        };

        abstract fun print(tag: String, msg: String): Int
    }

    private fun dump(
        msg: String,
        hasher: LogHasher,
        dumpable: Array<out Any?>
    ): String {
        val arguments = dumpable.mapIndexed { index, obj ->
            dumpObject(StringBuilder(), hasher, obj)?.toString()
                ?: "Argument @ index $index could not be dumped"
        }

        return msg.format(*arguments.toTypedArray())
    }

    private fun dump(
        msg: String,
        dumpable: Array<out Any?>
    ) = dump(msg, LogHasher.NoOpHasher, dumpable) to dump(msg, logHasher, dumpable)

    private fun log(type: Type, msg: String, dumpable: Array<out Any?>) {
        val (normal, redacted) = dump(msg, dumpable)
        val now = LocalDateTime.now()
        val logMsg = "$now $prefix: $normal"

        kotlin.runCatching {
            linkSheetApp.write(logMsg)
        }.onFailure {
            it.printStackTrace()
        }

        type.print(prefix, normal)
    }

    fun verbose(msg: String, vararg dumpable: Any?) = log(Type.Verbose, msg, dumpable)
    fun info(msg: String, vararg dumpable: Any?) = log(Type.Info, msg, dumpable)
    fun debug(msg: String, vararg dumpable: Any?) = log(Type.Debug, msg, dumpable)
}