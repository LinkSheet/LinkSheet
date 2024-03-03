package fe.linksheet.module.log.impl.internal

import fe.linksheet.module.log.file.FileAppLogger
import fe.linksheet.module.log.impl.hasher.HashProcessor
import fe.linksheet.module.redactor.Redactor
import kotlin.reflect.KClass

class DefaultLoggerDelegate(
    prefix: String,
    redactor: Redactor,
    fileAppLogger: FileAppLogger
) : LoggerDelegate(prefix, redactor, fileAppLogger) {
    constructor(
        clazz: KClass<*>,
        redactor: Redactor,
        fileAppLogger: FileAppLogger
    ) : this(clazz.simpleName!!, redactor, fileAppLogger)

    override fun <T> redactParameter(msg: ProduceMessage, param: T, processor: HashProcessor<T>): Pair<String, String> {
        val plain = redactor.process(false, param, processor)
        val redacted = redactor.process(true, param, processor)

        return msg(plain) to msg(redacted)
    }
}
