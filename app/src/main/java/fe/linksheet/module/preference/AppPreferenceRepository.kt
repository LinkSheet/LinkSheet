package fe.linksheet.module.preference

import android.content.Context
import com.google.gson.Gson
import fe.android.preference.helper.BasePreference
import fe.android.preference.helper.PreferenceRepository
import fe.android.preference.helper.compose.getBooleanState
import fe.linksheet.LinkSheetAppConfig
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val preferenceRepositoryModule = module {
    singleOf(::AppPreferenceRepository)
}

class AppPreferenceRepository(context: Context, val gson: Gson) : PreferenceRepository(context) {

    private val followRedirectsExternalService = getBooleanState(AppPreferences.followRedirectsExternalService)
    private val amp2HtmlExternalService = getBooleanState(AppPreferences.amp2HtmlExternalService)

    init {
        // Ensure backwards compatibility as this feature was previously included in non-pro versions
        if (!LinkSheetAppConfig.isPro()) {
            followRedirectsExternalService.updateState(false)
            amp2HtmlExternalService.updateState(false)
        }
    }

    fun dumpPreferences(excludePreferences: List<BasePreference<*, *>>): Map<String, String?> {
        return AppPreferences.all.toMutableMap().apply {
            excludePreferences.forEach { remove(it.key) }
        }.map { (_, pkg) -> pkg }.associate { preference ->
            preference.key to getAnyAsString(preference)
        }
    }
}