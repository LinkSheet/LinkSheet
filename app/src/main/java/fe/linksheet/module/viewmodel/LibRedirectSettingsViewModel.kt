package fe.linksheet.module.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import fe.libredirectkt.LibRedirectLoader
import fe.linksheet.module.repository.LibRedirectDefaultRepository
import fe.linksheet.module.repository.LibRedirectStateRepository
import fe.linksheet.util.flowOfLazy
import kotlinx.coroutines.flow.map

class LibRedirectSettingsViewModel(
    val context: Application,
    private val defaultRepository: LibRedirectDefaultRepository,
    private val stateRepository: LibRedirectStateRepository,
) : ViewModel() {

    private val libRedirectBuiltInServices = flowOfLazy { LibRedirectLoader.loadBuiltInServices() }

    val services = libRedirectBuiltInServices.map { services ->
        services.map {
            it to if (stateRepository.isEnabled(it.key)) defaultRepository.getInstanceUrl(it.key) else null
        }
    }
}