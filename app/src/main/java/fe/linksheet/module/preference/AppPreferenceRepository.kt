package fe.linksheet.module.preference

import android.content.Context
import com.google.gson.Gson
import fe.android.preference.helper.BasePreference
import fe.android.preference.helper.PreferenceRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val preferenceRepositoryModule = module {
    singleOf(::AppPreferenceRepository)
}

class AppPreferenceRepository(context: Context, val gson: Gson) : PreferenceRepository(context) {

    fun dumpPreferences(excludePreferences: List<BasePreference<*, *>>): Map<String, String?> {
        return AppPreferences.all.toMutableMap().apply {
            excludePreferences.forEach { remove(it.key) }
        }.map { (_, pkg) -> pkg }.associate { preference ->
            preference.key to getAnyAsString(preference)
        }
    }
}