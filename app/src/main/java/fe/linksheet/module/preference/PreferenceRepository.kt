package fe.linksheet.module.preference

import android.content.Context
import fe.android.preference.helper.compose.ComposePreferenceRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val featureFlagRepositoryModule = module {
    singleOf(::FeatureFlagRepository)
}

class FeatureFlagRepository(context: Context) : ComposePreferenceRepository(context) {

}
