package fe.linksheet.module.preference

import app.linksheet.api.PreferenceRegistry
import fe.android.preference.helper.Preference
import fe.android.preference.helper.PreferenceDefinition
import fe.android.preference.helper.TypeMapper
import kotlin.reflect.KClass

abstract class LinkSheetPreferenceDefinition(vararg blacklistedKeys: String) : PreferenceDefinition(*blacklistedKeys){
    val registry = object : PreferenceRegistry {
        override fun boolean(
            key: String,
            default: Boolean
        ): Preference.Boolean {
            return this@LinkSheetPreferenceDefinition.boolean(key, default)
        }

        override fun string(key: String, default: String?): Preference.Nullable<String> {
            return this@LinkSheetPreferenceDefinition.string(key, default)
        }

        override fun string(
            key: String,
            initial: () -> String
        ): Preference.Init {
            return this@LinkSheetPreferenceDefinition.string(key, initial)
        }

        override fun int(key: String, default: Int): Preference.Int {
            return this@LinkSheetPreferenceDefinition.int(key, default)
        }

        override fun <T : Any, M : Any> mapped(
            key: String,
            default: T,
            mapper: TypeMapper<T, M>,
            t: KClass<T>,
            m: KClass<M>
        ): Preference.Mapped<T, M> {
            return this@LinkSheetPreferenceDefinition.mapped(key, default, mapper, t, m)
        }
    }
}
