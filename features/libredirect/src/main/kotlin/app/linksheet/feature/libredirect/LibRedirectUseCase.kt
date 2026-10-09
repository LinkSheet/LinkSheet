package app.linksheet.feature.libredirect

import fe.libredirectkt.LibRedirectInstance
import fe.libredirectkt.LibRedirectLoader
import fe.libredirectkt.LibRedirectService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LibRedirectUseCase(
    private val _loadBuiltInServices: () -> List<LibRedirectService> = LibRedirectLoader::loadBuiltInServices,
    private val _loadBuiltInInstances: () -> List<LibRedirectInstance> = LibRedirectLoader::loadBuiltInInstances,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun loadBuiltInServices() = withContext(ioDispatcher){
        _loadBuiltInServices()
    }

    suspend fun loadBuiltInInstances() = withContext(ioDispatcher){
        _loadBuiltInInstances()
    }
}
