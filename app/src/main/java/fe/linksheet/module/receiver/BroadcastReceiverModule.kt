package fe.linksheet.module.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.LifecycleOwner
import fe.android.lifecycle.LifecycleAwareService
import fe.android.lifecycle.koin.extension.service
import fe.linksheet.BroadcastEventBus
import fe.linksheet.IntentEventHandler
import org.koin.dsl.bind
import org.koin.dsl.module

val BroadcastEventBusModule = module {
//    single { PackageStateChangedReceiver() }
    service<BroadcastReceiverService> {
        BroadcastReceiverService(
            registerReceiver = applicationContext::registerReceiver,
            unregisterReceiver = applicationContext::unregisterReceiver,
        )
    }.bind<BroadcastEventBus>()
}

class BroadcastState(
    val handlers: MutableList<IntentEventHandler>,
) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        for (handler in handlers) {
            handler.onReceive(context, intent)
        }
    }
}

class BroadcastReceiverService(
    private val registerReceiver: (BroadcastReceiver, IntentFilter) -> Unit,
    private val unregisterReceiver: (BroadcastReceiver) -> Unit,
) : BroadcastEventBus, LifecycleAwareService {
    private val map = mutableMapOf<IntentFilter, BroadcastState>()

    override suspend fun onAppInitialized(owner: LifecycleOwner) {
    }

    override fun register(handler: IntentEventHandler) {
        var state = map[handler.filter]
        if (state == null) {
            state = BroadcastState(mutableListOf(handler))
            registerReceiver(state, handler.filter)
        } else {
            state.handlers.add(handler)
        }
    }

    override fun unregister(handler: IntentEventHandler) {
        val state = map[handler.filter]
        if (state != null) {
            state.handlers.remove(handler)
            if (state.handlers.isEmpty()) {
                unregisterReceiver(state)
                map.remove(handler.filter)
            }
        }
    }
}

//class PackageStateChangedReceiver : BroadcastReceiver() {
//
//    override fun onReceive(context: Context, intent: Intent) {
//        if (intent.action == Intent.ACTION_PACKAGE_ADDED) {
//            val packageName = intent.data!!.schemeSpecificPart
//
//            if (AndroidVersion.isAtLeastApi31S()) {
//                Log.d("PackageStateChangedReceiver", "Disabling link handling for $packageName")
//                ShizukuDomainVerification().setDomainVerificationLinkHandlingAllowed(packageName, false)
//
//                Toast.makeText(context, "Disabled link handling for $packageName", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//}
