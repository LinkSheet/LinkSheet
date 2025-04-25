package fe.linksheet.module.log.internal

import fe.linksheet.module.log.file.LogPersistService
import fe.linksheet.module.redactor.HashProcessor
import fe.linksheet.module.redactor.Redactor
import kotlin.reflect.KClass

class DefaultLoggerDelegate(
    isDebug: Boolean,
    prefix: String,
    redactor: Redactor,
    logPersistService: LogPersistService
) : LoggerDelegate(isDebug, prefix, redactor, logPersistService) {

    constructor(
        isDebug: Boolean,
        clazz: KClass<*>,
        redactor: Redactor,
        logStorageService: LogPersistService,
    ) : this(isDebug, clazz.simpleName!!, redactor, logStorageService)

    override fun <T> redactParameter(param: T, processor: HashProcessor<T>): RedactedParameter {
        val plain = Redactor.NoOp.processToString(param, processor)
        val redacted = redactor.processToString(param, processor)

        return RedactedParameter(plain, redacted)
    }
}
