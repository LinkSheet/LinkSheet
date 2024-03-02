package fe.linksheet.extension.koin

import fe.linksheet.module.log.impl.Logger
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import kotlin.reflect.KClass

class ExtendedScope<T : Any>(val scope: Scope, private val clazz: KClass<T>) {
    val singletonLogger by lazy { scope.get<Logger>(parameters = { parametersOf(clazz) }) }

//    operator fun <X : Any> MappedPreference<X, String>.getValue(
//        thisObj: Any?,
//        property: KProperty<*>
//    ): StateMappedPreference<X, String> {
//        val preferenceRepository = scope.get<AppPreferenceRepository>()
//        return preferenceRepository.getState(thisObj as MappedPreference<X, String>)
//    }

//    fun createLogger(): Logger {
//        return scope.get<Logger>(parameters = { parametersOf(clazz) })
//    }
}
