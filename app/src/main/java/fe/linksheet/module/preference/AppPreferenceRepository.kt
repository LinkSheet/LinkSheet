package fe.linksheet.module.preference

import android.content.Context
import fe.android.preference.helper.PreferenceRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val preferenceRepositoryModule = module {
    singleOf(::AppPreferenceRepository)
}

class AppPreferenceRepository(context: Context) : PreferenceRepository(context) {
}