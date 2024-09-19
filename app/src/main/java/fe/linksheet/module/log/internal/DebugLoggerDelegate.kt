package fe.linksheet.module.log.internal

import fe.linksheet.module.log.Logger
import fe.linksheet.module.log.file.DebugLogPersistService
import fe.linksheet.module.log.file.LogPersistService
import fe.linksheet.module.redactor.HashProcessor
import fe.linksheet.module.redactor.Redactor
import org.koin.dsl.module
import kotlin.reflect.KClass

class DebugLoggerDelegate(
    prefix: String,
    redactor: Redactor = Redactor.NoOp,
    logPersistService: LogPersistService = DebugLogPersistService(),
) : LoggerDelegate(prefix, redactor, logPersistService) {

    constructor(
        clazz: KClass<*>,
        redactor: Redactor = Redactor.NoOp,
        logPersistService: LogPersistService = DebugLogPersistService(),
    ) : this(clazz.simpleName!!, redactor, logPersistService)

    companion object {
        val Factory = module {
            factory<Logger> { params ->
                val delegate = DebugLoggerDelegate(params.get<KClass<*>>())
                Logger(delegate)
            }
        }
    }

    override fun <T> redactParameter(param: T, processor: HashProcessor<T>): RedactedParameter {
        val plain = Redactor.NoOp.processToString(param, processor)
        return RedactedParameter(plain, plain)
    }
}
