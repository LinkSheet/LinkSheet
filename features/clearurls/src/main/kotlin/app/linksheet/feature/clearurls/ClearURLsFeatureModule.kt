package app.linksheet.feature.clearurls

import app.linksheet.feature.clearurls.core.ClearUrlsProviderLoader
import app.linksheet.feature.clearurls.viewmodel.ClearURLsSettingsViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val ClearURLsFeatureModule = module {
    single { ClearUrlsProviderLoader(context = get(), ioDispatcher = Dispatchers.IO) }
    viewModelOf(::ClearURLsSettingsViewModel)
}
