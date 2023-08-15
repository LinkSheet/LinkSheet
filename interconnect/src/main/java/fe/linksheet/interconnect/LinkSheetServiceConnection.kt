package fe.linksheet.interconnect

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder

/**
 * Wraps the connection and binder for the interconnect service,
 * and provides a convenience function for the client to disconnect.
 */
abstract class LinkSheetServiceConnection : ServiceConnection, ILinkSheetService {
    internal var service: ILinkSheetService? = null

    /**
     * Clients should call this when they're done
     * using the service. Once this is called,
     * the methods in this class are no longer usable.
     */
    abstract fun disconnect()

    final override fun onServiceDisconnected(name: ComponentName?) {
        service = null
    }

    final override fun getSelectedDomains(packageName: String?): StringParceledListSlice {
        assertService()
        return service!!.getSelectedDomains(packageName)
    }

    final override fun selectDomains(
        packageName: String?,
        domains: StringParceledListSlice?,
        componentName: ComponentName?
    ) {
        assertService()
        service?.selectDomains(packageName, domains, componentName)
    }

    final override fun asBinder(): IBinder {
        assertService()
        return service!!.asBinder()
    }

    private fun assertService() {
        if (service == null) {
            throw IllegalStateException("Service not bound!")
        }
    }
}
