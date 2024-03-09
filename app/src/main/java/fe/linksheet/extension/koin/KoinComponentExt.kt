package fe.linksheet.extension.koin

import fe.linksheet.module.log.Logger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.koin.mp.KoinPlatformTools
import kotlin.reflect.KClass

fun KoinComponent.injectLogger(
    clazz: KClass<*>,
    mode: LazyThreadSafetyMode = KoinPlatformTools.defaultLazyMode()
): Lazy<Logger> {
    return inject<Logger>(mode = mode, parameters = { parametersOf(clazz) })
}

inline fun <reified T> KoinComponent.injectLogger(
    mode: LazyThreadSafetyMode = KoinPlatformTools.defaultLazyMode()
): Lazy<Logger> {
    return injectLogger(T::class, mode)
}
