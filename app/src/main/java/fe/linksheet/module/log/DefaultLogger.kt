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

    abstract fun <T> verbose(msg: (String) -> String, param: T, hashProcessor: HashProcessor<T>)
    abstract fun verbose(throwable: Throwable)
    abstract fun verbose(msg: String)
    abstract fun <T> info(msg: (String) -> String, param: T, hashProcessor: HashProcessor<T>)
    abstract fun info(throwable: Throwable)
    abstract fun info(msg: String)

    abstract fun <T> debug(msg: (String) -> String, param: T, hashProcessor: HashProcessor<T>)
    abstract fun debug(throwable: Throwable)
    abstract fun debug(msg: String)
}

class DebugLogger(private val prefix: String) : Logger(LogHasher.NoOpHasher) {
    constructor(clazz: KClass<*>) : this(clazz.simpleName!!)

    override fun print(type: Type, msg: String) {
        println("${type.code} $prefix: $msg")
    }

    override fun <T> verbose(msg: (String) -> String, param: T, hashProcessor: HashProcessor<T>) {
        print(Type.Verbose, msg(param.toString()))
    }

    override fun verbose(throwable: Throwable) {
        print(Type.Verbose, throwable.asString())
    }

    override fun verbose(msg: String) {
        print(Type.Verbose, msg)
    }

    override fun <T> info(msg: (String) -> String, param: T, hashProcessor: HashProcessor<T>) {
        print(Type.Info, msg(param.toString()))
    }

    override fun info(throwable: Throwable) {
        print(Type.Info, throwable.asString())
    }

    override fun info(msg: String) {
        print(Type.Info, msg)
    }

    override fun <T> debug(msg: (String) -> String, param: T, hashProcessor: HashProcessor<T>) {
        print(Type.Debug, msg(param.toString()))
    }

    override fun debug(throwable: Throwable) {
        print(Type.Debug, throwable.asString())
    }

    override fun debug(msg: String) {
        print(Type.Debug, msg)
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
        hashProcessor: HashProcessor<T>
    ) {
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


    override fun <T> verbose(
        msg: (String) -> String,
        param: T,
        hashProcessor: HashProcessor<T>
    ) = log(Type.Verbose, msg, param, hashProcessor)

    override fun verbose(
        throwable: Throwable
    ) = log(Type.Verbose, throwable.asString(), prefix)

    override fun verbose(msg: String) {
        log(Type.Verbose, msg)
    }


    override fun <T> info(
        msg: (String) -> String,
        param: T,
        hashProcessor: HashProcessor<T>
    ) = log(Type.Info, msg, param, hashProcessor)

    override fun info(
        throwable: Throwable
    ) = log(Type.Info, throwable.asString(), prefix)

    override fun info(msg: String) {
        log(Type.Info, msg)
    }

    override fun <T> debug(
        msg: (String) -> String,
        param: T,
        hashProcessor: HashProcessor<T>
    ) = log(Type.Debug, msg, param, hashProcessor)

    override fun debug(
        throwable: Throwable
    ) = log(Type.Debug, throwable.asString(), prefix)

    override fun debug(msg: String) {
        log(Type.Debug, msg)
    }
}