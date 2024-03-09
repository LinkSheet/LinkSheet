package fe.linksheet.extension.koin

import fe.linksheet.module.log.Logger
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope

inline fun <reified T : Any> Scope.createLogger(): Logger {
    return get<Logger>(parameters = { parametersOf(T::class) })
}


