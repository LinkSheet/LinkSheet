package app.linksheet.service

import android.content.ComponentName
import fe.linksheet.interconnect.IDomainSelectionResultCallback
import fe.linksheet.interconnect.ILinkSheetService
import fe.linksheet.interconnect.ISelectedDomainsCallback
import fe.linksheet.interconnect.StringParceledListSlice
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface InterconnectHandler {
    fun verifyCaller(packageName: String)
    suspend fun getSelectedDomains(packageName: String): StringParceledListSlice
    fun startActivity(
        packageName: String,
        componentName: ComponentName,
        domains: StringParceledListSlice,
        callback: IDomainSelectionResultCallback?
    )
}

class InterconnectImpl(
    private val scope: CoroutineScope,
    private val handler: InterconnectHandler,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ILinkSheetService.Stub() {

    override fun getSelectedDomainsAsync(packageName: String, callback: ISelectedDomainsCallback) {
        handler.verifyCaller(packageName)
        scope.launch(dispatcher) {
            callback.onSelectedDomainsRetrieved(handler.getSelectedDomains(packageName))
        }
    }

    override fun selectDomains(
        packageName: String,
        domains: StringParceledListSlice,
        componentName: ComponentName,
    ) {
        handler.verifyCaller(packageName)
        handler.startActivity(packageName, componentName, domains, null)
    }

    override fun selectDomainsWithCallback(
        packageName: String,
        domains: StringParceledListSlice,
        componentName: ComponentName,
        callback: IDomainSelectionResultCallback,
    ) {
        handler.verifyCaller(packageName)
        handler.startActivity(packageName, componentName, domains, callback)
    }
}
