package fe.linksheet.module.log

import fe.linksheet.extension.koin.factory
import fe.linksheet.module.log.file.LogPersistService
import fe.linksheet.module.log.internal.DefaultLoggerDelegate
import fe.linksheet.module.log.internal.LoggerDelegate
import fe.linksheet.module.log.internal.ProduceMessage
import fe.linksheet.module.redactor.HashProcessor
import fe.linksheet.module.redactor.Redactor
import org.koin.dsl.module
import kotlin.reflect.KClass

val defaultLoggerModule = module {
    factory<Logger, Redactor, LogPersistService> { params, redactor, logFileService ->
        val delegate = DefaultLoggerDelegate(params.get<KClass<*>>(), redactor, logFileService)
        Logger(delegate)
    }
}

class Logger(private val delegate: LoggerDelegate) {
    fun fatal(stacktrace: String) {
        delegate.fatal(stacktrace)
    }

    fun <T> createContext(param: T, processor: HashProcessor<T>): LoggerDelegate.RedactedParameter {
        return delegate.createContext(param, processor)
    }

    /**
     * Verbose facades
     */
    fun verbose(param: LoggerDelegate.RedactedParameter, msg: ProduceMessage) {
        delegate.log(LoggerDelegate.Level.Verbose, param, msg, null)
    }

    fun <T> verbose(param: T, processor: HashProcessor<T>, msg: ProduceMessage) {
        delegate.log(LoggerDelegate.Level.Verbose, param, processor, msg, null)
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

    /**
     * Info facades
     */
    fun info(param: LoggerDelegate.RedactedParameter, msg: ProduceMessage) {
        delegate.log(LoggerDelegate.Level.Info, param, msg, null)
    }

    fun <T : Any> info(`return`: T?, param: LoggerDelegate.RedactedParameter, msg: ProduceMessage): T? {
        delegate.log(LoggerDelegate.Level.Info, param, msg, null)
        return `return`
    }

    fun <T> info(param: T, processor: HashProcessor<T>, msg: ProduceMessage) {
        delegate.log(LoggerDelegate.Level.Info, param, processor, msg, null)
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

    /**
     * Debug facades
     */
    fun debug(param: LoggerDelegate.RedactedParameter, msg: ProduceMessage) {
        delegate.log(LoggerDelegate.Level.Debug, param, msg, null)
    }

    fun <T> debug(param: T, processor: HashProcessor<T>, msg: ProduceMessage) {
        delegate.log(LoggerDelegate.Level.Debug, param, processor, msg, null)
    }

    fun <T> debug(param: T?, processor: HashProcessor<T>, msg: ProduceMessage, subPrefix: String? = null) {
        if (param != null) {
            delegate.log(LoggerDelegate.Level.Debug, param, processor, msg, subPrefix)
        } else {
            delegate.log(LoggerDelegate.Level.Debug, msg("<null>"), subPrefix = subPrefix)
        }
    }

    fun debug(msg: String? = null, throwable: Throwable? = null, subPrefix: String? = null) {
        delegate.log(LoggerDelegate.Level.Debug, msg, throwable, subPrefix)
    }

    fun debug(throwable: Throwable, subPrefix: String? = null) {
        delegate.log(LoggerDelegate.Level.Debug, throwable = throwable, subPrefix = subPrefix)
    }

    /**
     * Error facades
     */
    fun error(param: LoggerDelegate.RedactedParameter, msg: ProduceMessage) {
        delegate.log(LoggerDelegate.Level.Error, param, msg, null)
    }

    fun <T> error(param: T, processor: HashProcessor<T>, msg: ProduceMessage) {
        delegate.log(LoggerDelegate.Level.Error, param, processor, msg)
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

    inline fun <T, R, C : MutableCollection<in R>> mapNoException(
        iterable: Iterable<T>,
        destination: C,
        transform: (T) -> R,
    ): C {
        for (item in iterable) {
            try {
                destination.add(transform(item))
            } catch (e: Exception) {
                error(e)
            }
        }

        return destination
    }

    inline operator fun <R> invoke(
        `return`: R,
        param: LoggerDelegate.RedactedParameter,
        fn: (Logger, LoggerDelegate.RedactedParameter) -> Unit,
    ): R {
        fn(this, param)
        return `return`
    }

    inline operator fun <R> invoke(`return`: R, fn: (Logger) -> Unit): R {
        fn(this)
        return `return`
    }
}

