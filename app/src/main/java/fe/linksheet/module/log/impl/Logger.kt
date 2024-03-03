package fe.linksheet.module.log.impl

import fe.linksheet.extension.koin.factory
import fe.linksheet.module.log.file.FileAppLogger
import fe.linksheet.module.log.impl.hasher.HashProcessor
import fe.linksheet.module.log.impl.internal.DefaultLoggerDelegate
import fe.linksheet.module.log.impl.internal.LoggerDelegate
import fe.linksheet.module.log.impl.internal.ProduceMessage
import fe.linksheet.module.redactor.Redactor
import org.koin.dsl.module
import kotlin.reflect.KClass

val defaultLoggerModule = module {
    factory<Logger, Redactor, FileAppLogger> { params, redactor, fileAppLogger ->
        val delegate = DefaultLoggerDelegate(params.get<KClass<*>>(), redactor, fileAppLogger)
        Logger(delegate)
    }
}

class Logger(private val delegate: LoggerDelegate) {
    fun fatal(stacktrace: String) {
        delegate.fatal(stacktrace)
    }

    fun <T> verbose(param: T, processor: HashProcessor<T>, msg: ProduceMessage, subPrefix: String? = null) {
        delegate.log(LoggerDelegate.Level.Verbose, param, processor, msg, subPrefix)
    }

    fun verbose(msg: String? = null, throwable: Throwable? = null, subPrefix: String? = null) {
        delegate.log(LoggerDelegate.Level.Verbose, msg, throwable, subPrefix)
    }

    fun verbose(throwable: Throwable, subPrefix: String? = null) {
        delegate.log(LoggerDelegate.Level.Verbose, throwable = throwable, subPrefix = subPrefix)
    }

    fun <T> info(param: T, processor: HashProcessor<T>, msg: ProduceMessage, subPrefix: String? = null) {
        delegate.log(LoggerDelegate.Level.Info, param, processor, msg, subPrefix)
    }

    fun info(msg: String? = null, throwable: Throwable? = null, subPrefix: String? = null) {
        delegate.log(LoggerDelegate.Level.Info, msg, throwable, subPrefix)
    }

    fun info(throwable: Throwable, subPrefix: String? = null) {
        delegate.log(LoggerDelegate.Level.Info, throwable = throwable, subPrefix = subPrefix)
    }

    fun <T> debug(param: T, processor: HashProcessor<T>, msg: ProduceMessage, subPrefix: String? = null) {
        delegate.log(LoggerDelegate.Level.Debug, param, processor, msg, subPrefix)

    }

    fun debug(msg: String? = null, throwable: Throwable? = null, subPrefix: String? = null) {
        delegate.log(LoggerDelegate.Level.Debug, msg, throwable, subPrefix)
    }

    fun debug(throwable: Throwable, subPrefix: String? = null) {
        delegate.log(LoggerDelegate.Level.Debug, throwable = throwable, subPrefix = subPrefix)
    }

    fun <T> error(param: T, processor: HashProcessor<T>, msg: ProduceMessage, subPrefix: String? = null) {
        delegate.log(LoggerDelegate.Level.Error, param, processor, msg, subPrefix)
    }

    fun error(msg: String? = null, throwable: Throwable? = null, subPrefix: String? = null) {
        delegate.log(LoggerDelegate.Level.Error, msg, throwable, subPrefix)
    }

    fun error(throwable: Throwable, subPrefix: String? = null) {
        delegate.log(LoggerDelegate.Level.Error, throwable = throwable, subPrefix = subPrefix)
    }
}

