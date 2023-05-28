package fe.linksheet.module.log

import android.util.Log
import fe.linksheet.LinkSheetApp
import javax.crypto.Mac
import kotlin.reflect.KClass

class Logger(private val linkSheetApp: LinkSheetApp, private val prefix: String, mac: Mac) {
    constructor(
        context: LinkSheetApp,
        clazz: KClass<*>,
        mac: Mac
    ) : this(context, clazz.simpleName!!, mac)

    private val logHasher = LogHasher.LogKeyHasher(mac)
    private val appLogger = AppLogger.getInstance()

    enum class Type(val str: String) {
        Verbose("V") {
            override fun print(tag: String, msg: String): Int = Log.v(tag, msg)
        },
        Info("I") {
            override fun print(tag: String, msg: String): Int = Log.i(tag, msg)
        },
        Debug("D") {
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
            LogDumpable.dumpObject(StringBuilder(), hasher, obj)?.toString()
                ?: "Argument @ index $index could not be dumped (obj=$obj)"
        }

        return msg.format(*arguments.toTypedArray())
    }

    private fun <T> dump(
        msg: String,
        hasher: LogHasher,
        param: T,
        hashProcessor: HashProcessor<T>
    ) = msg.format(LogDumpable.dumpObject(StringBuilder(), hasher, param, hashProcessor).toString())

    private fun dump(
        msg: String,
        dumpable: Array<out Any?>
    ) = dump(msg, LogHasher.NoOpHasher, dumpable) to dump(msg, logHasher, dumpable)

    private fun <T> dump(
        msg: String,
        param: T,
        hashProcessor: HashProcessor<T>
    ) = dump(msg, LogHasher.NoOpHasher, param, hashProcessor) to dump(
        msg,
        logHasher,
        param,
        hashProcessor
    )

    private fun log(type: Type, msg: String, dumpable: Array<out Any?>) {
        val (normal, redacted) = dump(msg, dumpable)
        log(type, normal, redacted, prefix)
    }

    private fun <T> log(type: Type, msg: String, param: T, hashProcessor: HashProcessor<T>) {
        val (normal, redacted) = dump(msg, param, hashProcessor)
        log(type, normal, redacted, prefix)
    }

    private fun log(type: Type, normal: String, redacted: String, prefix: String) {
        appLogger.write(LogEntry(type.str, System.currentTimeMillis(), prefix, normal, redacted))
        type.print(prefix, normal)
    }

    fun verbose(msg: String, vararg dumpable: Any?) = log(Type.Verbose, msg, dumpable)
    fun info(msg: String, vararg dumpable: Any?) = log(Type.Info, msg, dumpable)
    fun debug(msg: String, vararg dumpable: Any?) = log(Type.Debug, msg, dumpable)
    fun <T> debug(
        msg: String,
        param: T,
        hashProcessor: HashProcessor<T>
    ) = log(Type.Debug, msg, param, hashProcessor)
}