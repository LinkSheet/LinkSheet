package fe.linksheet.module.preference

import android.content.Context
import com.google.gson.Gson
import fe.android.preference.helper.PreferenceRepository
import org.json.JSONObject
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val preferenceRepositoryModule = module {
    singleOf(::AppPreferenceRepository)
}

class AppPreferenceRepository(context: Context, val gson: Gson) : PreferenceRepository(context) {
}