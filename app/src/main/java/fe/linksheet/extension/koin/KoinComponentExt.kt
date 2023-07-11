package fe.linksheet.extension.koin

import fe.linksheet.module.log.LoggerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.mp.KoinPlatformTools
import kotlin.reflect.KClass

fun KoinComponent.injectLogger(
    clazz: KClass<*>,
    mode: LazyThreadSafetyMode = KoinPlatformTools.defaultLazyMode(),
) = lazy(mode) {
    get<LoggerFactory>().createLogger(clazz)
}