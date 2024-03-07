package fe.linksheet.module.log.impl.internal

import fe.linksheet.module.log.file.LogFileService
import fe.linksheet.module.redactor.HashProcessor
import fe.linksheet.module.redactor.Redactor
import kotlin.reflect.KClass

class DefaultLoggerDelegate(
    prefix: String,
    redactor: Redactor,
    fileAppLogger: LogFileService
) : LoggerDelegate(prefix, redactor, fileAppLogger) {
    constructor(
        clazz: KClass<*>,
        redactor: Redactor,
        fileAppLogger: LogFileService
    ) : this(clazz.simpleName!!, redactor, fileAppLogger)

//    override fun <T> redactParameter(msg: ProduceMessage, param: T, processor: HashProcessor<T>): Pair<String, String> {
//        TODO("Not yet implemented")
//    }

    override fun <T> redactParameter(msg: ProduceMessage, param: T, processor: HashProcessor<T>): Pair<String, String> {
        val plain = Redactor.NoOp.processToString(param, processor)
        val redacted = redactor.processToString(param, processor)

        return msg(plain) to msg(redacted)
    }
}
