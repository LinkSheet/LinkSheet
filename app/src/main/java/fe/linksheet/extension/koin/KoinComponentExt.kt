package fe.linksheet.extension.koin

import fe.linksheet.module.log.factory.LoggerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.mp.KoinPlatformTools
import kotlin.reflect.KClass

fun KoinComponent.injectLogger(
    clazz: KClass<*>,
    mode: LazyThreadSafetyMode = KoinPlatformTools.defaultLazyMode(),
) = lazy(mode) {
    get<LoggerFactory>().createLogger(clazz)
}
