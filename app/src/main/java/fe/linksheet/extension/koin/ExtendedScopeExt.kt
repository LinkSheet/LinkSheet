package fe.linksheet.extension.koin

import fe.droidkit.koin.ExtendedScope
import fe.linksheet.module.log.Logger
import org.koin.core.parameter.parametersOf

val <T : Any> ExtendedScope<T>.logger: Logger
    get() = scope.get<Logger>(parameters = { parametersOf(clazz) })
