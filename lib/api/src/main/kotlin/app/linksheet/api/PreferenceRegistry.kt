package app.linksheet.api

import fe.android.preference.helper.Preference
import fe.android.preference.helper.TypeMapper
import kotlin.reflect.KClass

interface PreferenceRegistry {
    fun boolean(key: String, default: Boolean = false): Preference.Boolean
    fun <T : Any, M : Any> mapped(
        key: String,
        default: T,
        mapper: TypeMapper<T, M>,
        t: KClass<T>,
        m: KClass<M>,
    ): Preference.Mapped<T, M>
}

public inline fun <reified T : Any, reified M : Any> PreferenceRegistry.mapped(
    key: String,
    default: T,
    mapper: TypeMapper<T, M>,
): Preference.Mapped<T, M> {
    return `access$mapped`(key, default, mapper, T::class, M::class)
}

@PublishedApi
internal fun <T : Any, M : Any> PreferenceRegistry.`access$mapped`(
    key: String,
    default: T,
    mapper: TypeMapper<T, M>,
    t: KClass<T>,
    m: KClass<M>,
): Preference.Mapped<T, M> = mapped(key, default, mapper, t, m)
