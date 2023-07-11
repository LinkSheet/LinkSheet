package fe.linksheet.module.log

import android.util.Log
import fe.kotlin.extension.asString
import kotlin.reflect.KClass

abstract class Logger(val hasher: LogHasher) {
    enum class Type(val code: String) {
        Verbose("V"), Info("I"), Debug("D")
    }

    abstract fun print(type: Type, msg: String)

    fun <T> dumpParameterToString(
        redact: Boolean = false,
        param: T,
        hashProcessor: HashProcessor<T>
    ) = dumpParameterToString(if (redact) hasher else LogHasher.NoOpHasher, param, hashProcessor)

    private fun <T> dumpParameterToString(
        hasher: LogHasher,
        param: T,
        hashProcessor: HashProcessor<T>
    ) = LogDumpable.dumpObject(StringBuilder(), hasher, param, hashProcessor).toString()

    abstract fun verbose(msg: String, vararg dumpable: Any?)
    abstract fun <T> verbose(msg: String, param: T, hashProcessor: HashProcessor<T>)
    abstract fun verbose(throwable: Throwable)

    abstract fun info(msg: String, vararg dumpable: Any?)
    abstract fun <T> info(msg: String, param: T, hashProcessor: HashProcessor<T>)
    abstract fun info(throwable: Throwable)

    abstract fun debug(msg: String, vararg dumpable: Any?)
    abstract fun <T> debug(msg: String, param: T, hashProcessor: HashProcessor<T>)
    abstract fun debug(throwable: Throwable)
}

class DebugLogger(private val prefix: String) : Logger(LogHasher.NoOpHasher) {
    constructor(clazz: KClass<*>) : this(clazz.simpleName!!)

    override fun print(type: Type, msg: String) {
        println("${type.code} $prefix: $msg")
    }

    override fun verbose(msg: String, vararg dumpable: Any?) {
        print(Type.Verbose, msg.format(*dumpable))
    }

    override fun <T> verbose(msg: String, param: T, hashProcessor: HashProcessor<T>) {
        print(Type.Verbose, msg.format(param))
    }

    override fun verbose(throwable: Throwable) {
        print(Type.Verbose, throwable.asString())
    }

    override fun info(msg: String, vararg dumpable: Any?) {
        print(Type.Info, msg.format(*dumpable))
    }

    override fun <T> info(msg: String, param: T, hashProcessor: HashProcessor<T>) {
        print(Type.Info, msg.format(param))
    }

    override fun info(throwable: Throwable) {
        print(Type.Info, throwable.asString())
    }

    override fun debug(msg: String, vararg dumpable: Any?) {
        print(Type.Debug, msg.format(*dumpable))
    }

    override fun <T> debug(msg: String, param: T, hashProcessor: HashProcessor<T>) {
        print(Type.Debug, msg.format(param))
    }

    override fun debug(throwable: Throwable) {
        print(Type.Debug, throwable.asString())
    }
}

class DefaultLogger(
    private val prefix: String,
    hasher: LogHasher.LogKeyHasher
) : Logger(hasher) {
    constructor(clazz: KClass<*>, hasher: LogHasher.LogKeyHasher) : this(clazz.simpleName!!, hasher)

    private val appLogger = AppLogger.getInstance()

    override fun print(type: Type, msg: String) {
        when (type) {
            Type.Verbose -> Log.v(prefix, msg)
            Type.Info -> Log.i(prefix, msg)
            Type.Debug -> Log.d(prefix, msg)
        }
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

    private fun dump(
        msg: String,
        dumpable: Array<out Any?>
    ) = dump(msg, LogHasher.NoOpHasher, dumpable) to dump(msg, hasher, dumpable)

    private fun <T> dump(
        msg: String,
        param: T,
        hashProcessor: HashProcessor<T>
    ) = msg.format(dumpParameterToString(false, param, hashProcessor)) to
            msg.format(dumpParameterToString(true, param, hashProcessor))


    private fun log(type: Type, msg: String, dumpable: Array<out Any?>) {
        val (normal, redacted) = dump(msg, dumpable)
        log(type, normal, redacted)
    }

    private fun <T> log(type: Type, msg: String, param: T, hashProcessor: HashProcessor<T>) {
        val (normal, redacted) = dump(msg, param, hashProcessor)
        log(type, normal, redacted)
    }

    private fun log(type: Type, normal: String, redacted: String) {
        appLogger.write(LogEntry(type.code, System.currentTimeMillis(), prefix, normal, redacted))
        print(type, normal)
    }

    private fun log(
        type: Type,
        normalAndRedacted: String,
    ) = log(type, normalAndRedacted, normalAndRedacted)

    override fun verbose(
        msg: String,
        vararg dumpable: Any?
    ) = log(Type.Verbose, msg, dumpable)

    override fun <T> verbose(
        msg: String,
        param: T,
        hashProcessor: HashProcessor<T>
    ) = log(Type.Verbose, msg, param, hashProcessor)

    override fun verbose(
        throwable: Throwable
    ) = log(Type.Verbose, throwable.asString(), prefix)


    override fun info(msg: String, vararg dumpable: Any?) = log(Type.Info, msg, dumpable)

    override fun <T> info(
        msg: String,
        param: T,
        hashProcessor: HashProcessor<T>
    ) = log(Type.Info, msg, param, hashProcessor)

    override fun info(
        throwable: Throwable
    ) = log(Type.Info, throwable.asString(), prefix)

    override fun debug(msg: String, vararg dumpable: Any?) = log(Type.Debug, msg, dumpable)

    override fun <T> debug(
        msg: String,
        param: T,
        hashProcessor: HashProcessor<T>
    ) = log(Type.Debug, msg, param, hashProcessor)

    override fun debug(
        throwable: Throwable
    ) = log(Type.Debug, throwable.asString(), prefix)
}