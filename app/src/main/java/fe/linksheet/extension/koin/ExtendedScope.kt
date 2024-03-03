package fe.linksheet.extension.koin

import fe.linksheet.LinkSheetApp
import fe.linksheet.module.lifecycle.AppLifecycleObserver
import fe.linksheet.module.log.impl.Logger
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import kotlin.reflect.KClass

class ExtendedScope<T : Any>(val scope: Scope, private val clazz: KClass<T>) {
    val serviceLogger by scope.inject<Logger>(parameters = { parametersOf(clazz) })
    val applicationContext by scope.inject<LinkSheetApp>()
    val applicationLifecycle by scope.inject<AppLifecycleObserver>()
}
