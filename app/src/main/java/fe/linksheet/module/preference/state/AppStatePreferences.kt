package fe.linksheet.module.preference.state

import app.linksheet.api.PreferenceRegistry
import app.linksheet.feature.remoteconfig.preference.remoteConfigStatePreferences
import fe.android.preference.helper.Preference
import fe.android.preference.helper.PreferenceDefinition
import fe.android.preference.helper.TypeMapper
import kotlin.reflect.KClass

object AppStatePreferences : PreferenceDefinition() {
    private val registry = object : PreferenceRegistry {
        override fun boolean(
            key: String,
            default: Boolean
        ): Preference.Boolean {
            return this@AppStatePreferences.boolean(key, default)
        }

        override fun string(key: String, default: String?): Preference.Nullable<String> {
            return this@AppStatePreferences.string(key, default)
        }

        override fun string(
            key: String,
            initial: () -> String
        ): Preference.Init {
            return this@AppStatePreferences.string(key, initial)
        }

        override fun int(key: String, default: Int): Preference.Int {
            return this@AppStatePreferences.int(key, default)
        }

        override fun <T : Any, M : Any> mapped(
            key: String,
            default: T,
            mapper: TypeMapper<T, M>,
            t: KClass<T>,
            m: KClass<M>
        ): Preference.Mapped<T, M> {
            return this@AppStatePreferences.mapped(key, default, mapper, t, m)
        }
    }

    val newDefaults_2024_12_16_InfoDismissed = boolean("has_new_defaults_2024_12_16_info_dismissed", true)
    val newDefaults_2024_12_29_InfoDismissed = boolean("has_new_defaults_2024_12_29_info_dismissed", true)
    val newDefaults_2025_12_15_InfoDismissed = boolean("has_new_defaults_2025_12_15_info_dismissed")

    val remoteConfig = remoteConfigStatePreferences(registry)

    @Suppress("ObjectPropertyName")
    object NewDefaults {
        val `2024-11-29` = long("has_new_defaults_2024_11_29")
        val `2024-11-30` = long("has_new_defaults_2024_11_30")
        val `2024-12-16` = long("has_new_defaults_2024_12_16")
        val `2025-07-29` = long("has_new_defaults_2025_07_29")
        val `2025-08-03` = long("has_new_defaults_2025_08_03")
        val `2025-12-15` = long("has_new_defaults_2025_12_15")
        val `2026-04-27` = long("has_new_defaults_2026_04_27")
    }

    val newDefaults = NewDefaults

    init {
        finalize()
    }
}
