package fe.linksheet.extension.koin

import fe.linksheet.lifecycle.Service
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.Qualifier

typealias DefinitionParam0<T> = ExtendedScope<T>.(ParametersHolder) -> T
typealias DefinitionParam1<T, P> = ExtendedScope<T>.(ParametersHolder, P) -> T
typealias DefinitionParam2<T, P, P2> = ExtendedScope<T>.(ParametersHolder, P, P2) -> T
typealias DefinitionParam3<T, P, P2, P3> = ExtendedScope<T>.(ParametersHolder, P, P2, P3) -> T

inline fun <reified T : Any, reified P : Any> Module.single(
    qualifier: Qualifier? = null,
    createdAtStart: Boolean = false,
    noinline definition: DefinitionParam1<T, P>
): KoinDefinition<T> {
    return single(qualifier, createdAtStart) { params -> definition(ExtendedScope(this, T::class), params, get<P>()) }
}

inline fun <reified T : Any, reified P : Any, reified P2 : Any> Module.single(
    qualifier: Qualifier? = null,
    createdAtStart: Boolean = false,
    noinline definition: DefinitionParam2<T, P, P2>
): KoinDefinition<T> {
    return single(qualifier, createdAtStart) { params ->
        definition(
            ExtendedScope(this, T::class),
            params,
            get<P>(),
            get<P2>()
        )
    }
}

inline fun <reified T : Any, reified P : Any, reified P2 : Any, reified P3 : Any> Module.single(
    qualifier: Qualifier? = null,
    createdAtStart: Boolean = false,
    noinline definition: DefinitionParam3<T, P, P2, P3>
): KoinDefinition<T> {
    return single(qualifier, createdAtStart) { params ->
        definition(
            ExtendedScope(this, T::class),
            params,
            get<P>(),
            get<P2>(),
            get<P3>()
        )
    }
}

inline fun <reified T : Service> Module.service(
    qualifier: Qualifier? = null,
    noinline definition: DefinitionParam0<T>
): KoinDefinition<T> {
    return single(qualifier, true) { params ->
        val scope = ExtendedScope(this, T::class)
        val service = definition(scope, params)

        scope.applicationLifecycle.register(service)
        service
    }
}

inline fun <reified T : Service, reified P : Any> Module.service(
    qualifier: Qualifier? = null,
    noinline definition: DefinitionParam1<T, P>
): KoinDefinition<T> {
    return single(qualifier, true) { params ->
        val scope = ExtendedScope(this, T::class)
        val service = definition(scope, params, get<P>())

        scope.applicationLifecycle.register(service)
        service
    }
}

inline fun <reified T : Service, reified P : Any, reified P2 : Any> Module.service(
    qualifier: Qualifier? = null,
    noinline definition: DefinitionParam2<T, P, P2>
): KoinDefinition<T> {
    return single(qualifier, true) { params ->
        val scope = ExtendedScope(this, T::class)
        val service = definition(scope, params, get<P>(), get<P2>())

        scope.applicationLifecycle.register(service)
        service
    }
}

inline fun <reified T : Any, reified P : Any> Module.factory(
    qualifier: Qualifier? = null,
    noinline definition: DefinitionParam1<T, P>
): KoinDefinition<T> {
    return factory(qualifier) { params -> definition(ExtendedScope(this, T::class), params, get<P>()) }
}

inline fun <reified T : Any, reified P : Any, reified P2 : Any> Module.factory(
    qualifier: Qualifier? = null,
    noinline definition: DefinitionParam2<T, P, P2>
): KoinDefinition<T> {
    return factory(qualifier) { params -> definition(ExtendedScope(this, T::class), params, get<P>(), get<P2>()) }
}
