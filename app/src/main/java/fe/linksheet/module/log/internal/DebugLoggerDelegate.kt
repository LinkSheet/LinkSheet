package fe.linksheet.module.log.internal

import fe.linksheet.extension.koin.factory
import fe.linksheet.module.log.file.LogFileService
import fe.linksheet.module.log.Logger
import fe.linksheet.module.redactor.HashProcessor
import fe.linksheet.module.redactor.Redactor
import org.koin.dsl.module
import kotlin.reflect.KClass

class DebugLoggerDelegate(
    prefix: String,
    redactor: Redactor,
    logStorageService: LogFileService
) : LoggerDelegate(prefix, redactor, logStorageService) {
    constructor(
        clazz: KClass<*>,
        redactor: Redactor,
        logStorageService: LogFileService
    ) : this(clazz.simpleName!!, redactor, logStorageService)

    companion object{
        val module = module {
            factory<Logger, Redactor, LogFileService> { params, redactor, storageService ->
                val delegate = DebugLoggerDelegate(params.get<KClass<*>>(), redactor, storageService)
                Logger(delegate)
            }
        }
    }

    override fun <T> redactParameter(msg: ProduceMessage, param: T, processor: HashProcessor<T>): Pair<String, String> {
        val plain = Redactor.NoOp.processToString(param, processor)
        val msgStr = msg(plain)

        return msgStr to msgStr
    }
}
