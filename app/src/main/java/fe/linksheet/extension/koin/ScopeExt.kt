package fe.linksheet.extension.koin

import android.content.Context
import androidx.core.content.ContextCompat
import fe.linksheet.module.log.Logger
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope

inline fun <reified T : Any> Scope.createLogger(): Logger {
    return get<Logger>(parameters = { parametersOf(T::class) })
}

inline fun <reified T : Any> Scope.getSystemServiceOrNull(): T? {
    return ContextCompat.getSystemService(get<Context>(), T::class.java)
}

inline fun <reified T : Any> Scope.getSystemServiceOrThrow(): T {
    return getSystemServiceOrNull()!!
}
