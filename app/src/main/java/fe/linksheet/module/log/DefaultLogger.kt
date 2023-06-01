package fe.linksheet.module.log

import android.util.Log
import javax.crypto.Mac
import kotlin.reflect.KClass

interface Logger {
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

    fun verbose(msg: String, vararg dumpable: Any?)
    fun info(msg: String, vararg dumpable: Any?)
    fun debug(msg: String, vararg dumpable: Any?)
    fun <T> debug(msg: String, param: T, hashProcessor: HashProcessor<T>)
}

class DebugLogger(private val prefix: String) : Logger {
    override fun verbose(msg: String, vararg dumpable: Any?) {
        Logger.Type.Verbose.print(prefix, msg.format(*dumpable))
    }

    override fun info(msg: String, vararg dumpable: Any?) {
        Logger.Type.Info.print(prefix, msg.format(*dumpable))
    }

    override fun debug(msg: String, vararg dumpable: Any?) {
        Logger.Type.Debug.print(prefix, msg.format(*dumpable))
    }

    override fun <T> debug(msg: String, param: T, hashProcessor: HashProcessor<T>) {
        Logger.Type.Debug.print(prefix, msg.format(param))
    }
}

class DefaultLogger(private val prefix: String, mac: Mac) : Logger {
    constructor(clazz: KClass<*>, mac: Mac) : this(clazz.simpleName!!, mac)

    private val logHasher = LogHasher.LogKeyHasher(mac)
    private val appLogger = AppLogger.getInstance()

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

    private fun log(type: Logger.Type, msg: String, dumpable: Array<out Any?>) {
        val (normal, redacted) = dump(msg, dumpable)
        log(type, normal, redacted, prefix)
    }

    private fun <T> log(type: Logger.Type, msg: String, param: T, hashProcessor: HashProcessor<T>) {
        val (normal, redacted) = dump(msg, param, hashProcessor)
        log(type, normal, redacted, prefix)
    }

    private fun log(type: Logger.Type, normal: String, redacted: String, prefix: String) {
        appLogger.write(LogEntry(type.str, System.currentTimeMillis(), prefix, normal, redacted))
        type.print(prefix, normal)
    }

    override fun verbose(msg: String, vararg dumpable: Any?) = log(Logger.Type.Verbose, msg, dumpable)
    override fun info(msg: String, vararg dumpable: Any?) = log(Logger.Type.Info, msg, dumpable)
    override fun debug(msg: String, vararg dumpable: Any?) = log(Logger.Type.Debug, msg, dumpable)
    override fun <T> debug(
        msg: String,
        param: T,
        hashProcessor: HashProcessor<T>
    ) = log(Logger.Type.Debug, msg, param, hashProcessor)
}