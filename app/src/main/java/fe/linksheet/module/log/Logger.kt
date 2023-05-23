package fe.linksheet.module.log

import android.util.Log
import fe.linksheet.extension.decodeHex
import fe.linksheet.module.log.LogDumpable.Companion.dumpObject
import fe.linksheet.module.preference.PreferenceRepository
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

        LoggerFactory(logKey)
    }
}

class LoggerFactory(private val logKey: ByteArray) {
    private val mac by lazy {
        CryptoUtil.makeHmac(loggerHmac.algorithm, logKey)
    }

    fun createLogger(prefix: KClass<*>) = Logger(prefix, mac)
    fun createLogger(prefix: String) = Logger(prefix, mac)
}

class Logger(private val prefix: String, mac: Mac) {
    constructor(clazz: KClass<*>, mac: Mac) : this(clazz.simpleName!!, mac)

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

        type.print(prefix, normal)
    }

    fun verbose(msg: String, vararg dumpable: Any?) = log(Type.Verbose, msg, dumpable)
    fun info(msg: String, vararg dumpable: Any?) = log(Type.Info, msg, dumpable)
    fun debug(msg: String, vararg dumpable: Any?) = log(Type.Debug, msg, dumpable)
}