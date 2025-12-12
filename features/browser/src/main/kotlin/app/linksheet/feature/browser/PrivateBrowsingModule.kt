package app.linksheet.feature.browser

import app.linksheet.feature.browser.core.PrivateBrowsingService
import app.linksheet.feature.browser.database.PrivateBrowsingDatabase
import app.linksheet.feature.browser.database.repository.PrivateBrowsingBrowserRepository
import app.linksheet.feature.browser.viewmodel.PrivateBrowsingBrowserSettingsViewModel
import app.linksheet.feature.browser.viewmodel.PrivateBrowsingSettingsViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val PrivateBrowsingModule = module {
    single<PrivateBrowsingDatabase> { PrivateBrowsingDatabase.create(context = get(), name = "private_browsing") }
    single {
        PrivateBrowsingService(context = get())
    }
    factory {
        PrivateBrowsingBrowserRepository(dao = get<PrivateBrowsingDatabase>().privateBrowsingBrowserDao())
    }
    viewModelOf(::PrivateBrowsingSettingsViewModel)
    viewModel {
        PrivateBrowsingBrowserSettingsViewModel(
            repository = get(),
            useCase = get(),
            privateBrowsingService = get(),
            dispatcher = Dispatchers.IO
        )
    }
}
