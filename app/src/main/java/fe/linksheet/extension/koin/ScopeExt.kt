package fe.linksheet.extension.koin

import fe.linksheet.module.log.factory.LoggerFactory
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope

inline fun <reified T : Any> Scope.createLogger(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
) = get<LoggerFactory>(qualifier, parameters).createLogger(T::class)
