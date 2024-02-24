package fe.linksheet.module.log.impl

import android.util.Log
import fe.linksheet.module.log.file.FileAppLogger
import fe.linksheet.module.log.file.entry.LogEntry
import fe.linksheet.module.log.impl.hasher.HashProcessor
import fe.linksheet.module.log.impl.hasher.LogDumpable
import fe.linksheet.module.log.impl.hasher.LogHasher
import kotlin.reflect.KClass


class DefaultLogger(
    prefix: String,
    hasher: LogHasher.LogKeyHasher,
    fileAppLogger: FileAppLogger
) : Logger(prefix, hasher, fileAppLogger) {
    constructor(
        clazz: KClass<*>,
        hasher: LogHasher.LogKeyHasher,
        fileAppLogger: FileAppLogger
    ) : this(clazz.simpleName!!, hasher, fileAppLogger)

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
        fileAppLogger.write(
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
