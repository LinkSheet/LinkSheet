package fe.linksheet.module.log

import android.util.Log
import fe.kotlin.extension.asString
import fe.linksheet.module.log.entry.LogEntry
import fe.linksheet.module.log.hasher.HashProcessor
import fe.linksheet.module.log.hasher.LogDumpable
import fe.linksheet.module.log.hasher.LogHasher
import kotlin.reflect.KClass

abstract class Logger(val prefix: String, val hasher: LogHasher) {
    protected val appLogger = AppLogger.getInstance()

    enum class Type(val code: String) {
        Verbose("V"), Info("I"), Debug("D"), Error("E")
    }

    protected fun mergeSubPrefix(msg: String, subPrefix: String?) = (subPrefix?.let { "[$it] " } ?: "") + msg

    protected abstract fun print(type: Type, msg: String, subPrefix: String? = null)

    fun logFatal(stacktrace: String): String {
        Log.wtf("Crash", stacktrace)

        appLogger.write(LogEntry.FatalEntry(System.currentTimeMillis(), stacktrace))
        return stacktrace
    }

    fun <T> dumpParameterToString(
        redact: Boolean = false,
        param: T,
        hashProcessor: HashProcessor<T>
    ): String {
        return dumpParameterToString(
            if (redact) hasher else LogHasher.NoOpHasher,
            param,
            hashProcessor
        )
    }

    private fun <T> dumpParameterToString(
        hasher: LogHasher,
        param: T,
        hashProcessor: HashProcessor<T>
    ): String {
        return LogDumpable.dumpObject(StringBuilder(), hasher, param, hashProcessor).toString()
            .replace("%", "%%")
    }

    abstract fun <T> verbose(
        msg: (String) -> String,
        param: T,
        hashProcessor: HashProcessor<T>,
        subPrefix: String? = null
    )

    abstract fun verbose(throwable: Throwable, subPrefix: String? = null)
    abstract fun verbose(msg: String, subPrefix: String? = null)
    abstract fun <T> info(
        msg: (String) -> String,
        param: T,
        hashProcessor: HashProcessor<T>,
        subPrefix: String? = null
    )

    abstract fun info(throwable: Throwable, subPrefix: String? = null)
    abstract fun info(msg: String, subPrefix: String? = null)

    abstract fun <T> debug(
        msg: (String) -> String,
        param: T,
        hashProcessor: HashProcessor<T>,
        subPrefix: String? = null
    )

    abstract fun debug(throwable: Throwable, subPrefix: String? = null)
    abstract fun debug(msg: String, subPrefix: String? = null)

    abstract fun <T> error(
        msg: (String) -> String,
        param: T,
        hashProcessor: HashProcessor<T>,
        subPrefix: String? = null
    )

    abstract fun error(throwable: Throwable, subPrefix: String? = null)
    abstract fun error(msg: String, subPrefix: String? = null)
}

class DebugLogger(prefix: String) : Logger(prefix, LogHasher.NoOpHasher) {
    constructor(clazz: KClass<*>) : this(clazz.simpleName!!)

    override fun print(type: Type, msg: String, subPrefix: String?) {
        print(type, "${mergeSubPrefix(msg, subPrefix)}: $msg")
    }

    override fun <T> verbose(
        msg: (String) -> String,
        param: T,
        hashProcessor: HashProcessor<T>,
        subPrefix: String?
    ) {
        print(Type.Verbose, msg(param.toString()), subPrefix)
    }

    override fun verbose(throwable: Throwable, subPrefix: String?) {
        print(Type.Verbose, Log.getStackTraceString(throwable), subPrefix)
    }

    override fun verbose(msg: String, subPrefix: String?) {
        print(Type.Verbose, msg, subPrefix)
    }

    override fun <T> info(
        msg: (String) -> String,
        param: T,
        hashProcessor: HashProcessor<T>,
        subPrefix: String?
    ) {
        print(Type.Info, msg(param.toString()), subPrefix)
    }

    override fun info(throwable: Throwable, subPrefix: String?) {
        print(Type.Info, Log.getStackTraceString(throwable), subPrefix)
    }

    override fun info(msg: String, subPrefix: String?) {
        print(Type.Info, msg, subPrefix)
    }

    override fun <T> debug(
        msg: (String) -> String,
        param: T,
        hashProcessor: HashProcessor<T>,
        subPrefix: String?
    ) {
        print(Type.Debug, msg(param.toString()), subPrefix)
    }

    override fun debug(throwable: Throwable, subPrefix: String?) {
        print(Type.Debug, Log.getStackTraceString(throwable), subPrefix)
    }

    override fun debug(msg: String, subPrefix: String?) {
        print(Type.Debug, msg, subPrefix)
    }

    override fun <T> error(
        msg: (String) -> String,
        param: T,
        hashProcessor: HashProcessor<T>,
        subPrefix: String?
    ) {
        print(Type.Error, msg(param.toString()), subPrefix)
    }

    override fun error(throwable: Throwable, subPrefix: String?) {
        print(Type.Error, Log.getStackTraceString(throwable), subPrefix)
    }

    override fun error(msg: String, subPrefix: String?) {
        print(Type.Error, msg, subPrefix)
    }
}

class DefaultLogger(
    prefix: String,
    hasher: LogHasher.LogKeyHasher
) : Logger(prefix, hasher) {
    constructor(clazz: KClass<*>, hasher: LogHasher.LogKeyHasher) : this(clazz.simpleName!!, hasher)

    override fun print(type: Type, msg: String, subPrefix: String?) {
        val mergedMessage = mergeSubPrefix(msg, subPrefix)
        when (type) {
            Type.Verbose -> Log.v(prefix, mergedMessage)
            Type.Info -> Log.i(prefix, mergedMessage)
            Type.Debug -> Log.d(prefix, mergedMessage)
            Type.Error -> Log.e(prefix, mergedMessage)
        }
    }

    private fun <T> dump(
        msg: (String) -> String,
        param: T,
        hasher: LogHasher,
    ): String {
//        val arguments = dumpable.mapIndexed { index, obj ->
        val argument = LogDumpable.dumpObject(StringBuilder(), hasher, param)?.toString()
            ?: "$param could not be dumped"
//                ?: "Argument @ index $index could not be dumped (obj=$obj)"
//        }

        return msg(argument)
//        return msg.format(*arguments.toTypedArray())
    }

    private fun <T> dump(
        msg: (String) -> String,
        param: T,
        hashProcessor: HashProcessor<T>
    ): Pair<String, String> {
        return msg(dumpParameterToString(false, param, hashProcessor)) to
                msg(dumpParameterToString(true, param, hashProcessor))
    }

    private fun <T> log(
        type: Type,
        msg: (String) -> String,
        param: T,
        hashProcessor: HashProcessor<T>,
        subPrefix: String?,
    ) {
        val (normal, redacted) = dump(msg, param, hashProcessor)
        log(type, normal, redacted, subPrefix)
    }

    private fun log(type: Type, normal: String, redacted: String, subPrefix: String?) {
        appLogger.write(
            LogEntry.DefaultLogEntry(
                type.code,
                System.currentTimeMillis(),
                prefix + (subPrefix?.let { "/$it" } ?: ""),
                normal,
                redacted))
        print(type, normal, subPrefix)
    }

    private fun log(
        type: Type,
        normalAndRedacted: String,
        subPrefix: String?
    ) = log(type, normalAndRedacted, normalAndRedacted, subPrefix)


    override fun <T> verbose(
        msg: (String) -> String,
        param: T,
        hashProcessor: HashProcessor<T>,
        subPrefix: String?
    ) = log(Type.Verbose, msg, param, hashProcessor, subPrefix)

    override fun verbose(
        throwable: Throwable,
        subPrefix: String?
    ) = log(Type.Verbose, Log.getStackTraceString(throwable), subPrefix)

    override fun verbose(msg: String, subPrefix: String?) {
        log(Type.Verbose, msg, subPrefix)
    }

    override fun <T> info(
        msg: (String) -> String,
        param: T,
        hashProcessor: HashProcessor<T>,
        subPrefix: String?
    ) = log(Type.Info, msg, param, hashProcessor, subPrefix)

    override fun info(
        throwable: Throwable,
        subPrefix: String?
    ) = log(Type.Info, Log.getStackTraceString(throwable), subPrefix)

    override fun info(msg: String, subPrefix: String?) {
        log(Type.Info, msg, subPrefix)
    }

    override fun <T> debug(
        msg: (String) -> String,
        param: T,
        hashProcessor: HashProcessor<T>,
        subPrefix: String?
    ) = log(Type.Debug, msg, param, hashProcessor, subPrefix)

    override fun debug(
        throwable: Throwable,
        subPrefix: String?
    ) = log(Type.Debug, Log.getStackTraceString(throwable), subPrefix)

    override fun debug(msg: String, subPrefix: String?) {
        log(Type.Debug, msg, subPrefix)
    }

    override fun <T> error(msg: (String) -> String, param: T, hashProcessor: HashProcessor<T>, subPrefix: String?) {
        return log(Type.Error, msg, param, hashProcessor, subPrefix)
    }

    override fun error(throwable: Throwable, subPrefix: String?) {
        log(Type.Error, Log.getStackTraceString(throwable), subPrefix)
    }

    override fun error(msg: String, subPrefix: String?) {
        log(Type.Error, msg, subPrefix)
    }
}
