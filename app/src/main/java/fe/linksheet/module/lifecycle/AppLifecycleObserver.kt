package fe.linksheet.module.lifecycle

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope

class AppLifecycleObserver(
    private val lifecycleObserver: LifecycleOwner
) : DefaultLifecycleObserver {
    private val services = mutableListOf<Service>()
    val coroutineScope = lifecycleObserver.lifecycleScope

    fun register(service: Service) {
        services.add(service)
    }

    fun attach() {
        lifecycleObserver.lifecycle.addObserver(this)
    }

    fun bootServices() {
        Log.d("AppLifecycle", "Booting ${services.size} services")
        services.forEach { it.start(lifecycleObserver.lifecycle) }
    }

//    override fun onCreate(owner: LifecycleOwner) {
//        Log.d("AppLifecycle", "Shutting down ${services.size} services")
//        services.forEach { it.boot(owner.lifecycle) }
//    }

    override fun onStop(owner: LifecycleOwner) {
        Log.d("AppLifecycle", "Shutting down ${services.size} services")
        services.forEach { it.stop(owner.lifecycle) }
    }
}
