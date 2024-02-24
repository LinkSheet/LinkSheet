package fe.linksheet.module.log.impl

import android.util.Log
import fe.linksheet.module.log.file.FileAppLogger
import fe.linksheet.module.log.impl.hasher.HashProcessor
import fe.linksheet.module.log.impl.hasher.LogHasher
import kotlin.reflect.KClass

class DebugLogger(prefix: String, fileAppLogger: FileAppLogger) : Logger(prefix, LogHasher.NoOpHasher, fileAppLogger) {
    constructor(clazz: KClass<*>, fileAppLogger: FileAppLogger) : this(clazz.simpleName!!, fileAppLogger)

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
