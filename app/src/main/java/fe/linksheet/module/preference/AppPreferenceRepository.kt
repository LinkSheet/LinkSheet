package fe.linksheet.module.preference

import android.content.Context
import com.google.gson.Gson
import fe.android.preference.helper.PreferenceRepository
import fe.android.preference.helper.compose.getBooleanState
import fe.linksheet.LinkSheetAppConfig
import org.json.JSONObject
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
}