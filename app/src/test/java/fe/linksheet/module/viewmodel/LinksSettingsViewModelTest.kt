package fe.linksheet.module.viewmodel

import android.app.Application
import app.linksheet.feature.clearurls.core.ClearUrlsProviderLoader
import androidx.lifecycle.ViewModelStore
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import fe.composekit.preference.asFunction
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.app.DefaultAppPreferenceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@Config(sdk = [35], application = Application::class)
class LinksSettingsViewModelTest {
    @Test
    fun parameterSelectionIsOptInAndPersists() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        val repository = DefaultAppPreferenceRepository(application)
        val preference = AppPreferences.bottomSheet.selectLinkParameters
        assertEquals(false, repository.get(preference))
        repository.put(preference, true)
        assertEquals(true, DefaultAppPreferenceRepository(application).get(preference))
        repository.put(preference, false)
    }

    @Test
    fun providerTogglesUpdateVisibleStateResolverAndStorage() = runBlocking {
        Dispatchers.setMain(Dispatchers.Unconfined)
        val store = ViewModelStore()
        try {
            val application = ApplicationProvider.getApplicationContext<Application>()
            val repository = DefaultAppPreferenceRepository(application)
            val preference = AppPreferences.clearUrlsRemoveAllQueryProviders
            repository.put(preference, emptySet())
            // The resolver can already be observing this preference before settings opens.
            val resolverSelection = repository.asFunction(preference)
            val model = LinksSettingsViewModel(application, repository, ClearUrlsProviderLoader(application, Dispatchers.IO))
            store.put("links", model)
            withTimeout(10_000) { model.clearUrlsProviders.first { !it.loading } }
            model.showClearUrlsProviders(true)
            model.searchClearUrlsProviders("insta")
            withTimeout(10_000) { model.clearUrlsProviders.first { it.providers == listOf("instagram") } }

            model.setRemoveAllQueryParameters("instagram", true)
            withTimeout(5_000) { model.clearUrlsProviders.first { "instagram" in it.selected } }
            assertEquals(setOf("instagram"), resolverSelection())
            assertEquals(setOf("instagram"), DefaultAppPreferenceRepository(application).get(preference))

            model.setRemoveAllQueryParameters("instagram", false)
            withTimeout(5_000) { model.clearUrlsProviders.first { it.selected.isEmpty() } }
            assertEquals(emptySet<String>(), resolverSelection())
            assertEquals(emptySet<String>(), repository.get(preference))
        } finally {
            store.clear()
            Dispatchers.resetMain()
        }
    }
}
