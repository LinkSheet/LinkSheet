package app.linksheet.service

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.os.*
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.core.graphics.drawable.IconCompat
import fe.linksheet.R
import fe.linksheet.activity.SelectDomainsConfirmationActivity
import fe.linksheet.interconnect.IDomainSelectionResultCallback
import fe.linksheet.interconnect.StringParceledListSlice
import fe.linksheet.module.repository.PreferredAppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import org.koin.android.ext.android.inject

class InterconnectService2 : Service(), CoroutineScope by MainScope() {
    private val preferredAppRepository: PreferredAppRepository by inject()

    private val handler = Handler(Looper.getMainLooper()) {
        if (it.what == SelectDomainsConfirmationActivity.MSG_CHOOSER_FINISHED) {
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
            return@Handler true
        }
        return@Handler false
    }
    private val messenger = Messenger(handler)
    private val interconnectHandler = object : InterconnectHandler {
        override fun verifyCaller(packageName: String) {
            val callingPackages = packageManager.getPackagesForUid(Binder.getCallingUid())
            if (callingPackages?.contains(packageName) != true) {
                throw IllegalAccessException("Calling package is not $packageName!")
            }
        }

        override suspend fun getSelectedDomains(packageName: String): StringParceledListSlice {
            return StringParceledListSlice(
                preferredAppRepository.getByPackageName(packageName).map { it.host }
            )
        }

        override fun startActivity(
            packageName: String,
            componentName: ComponentName,
            domains: StringParceledListSlice,
            callback: IDomainSelectionResultCallback?
        ) {
            SelectDomainsConfirmationActivity.start(
                this@InterconnectService2,
                packageName,
                componentName,
                domains,
                messenger,
                callback
            )
        }
    }

    override fun onCreate() {
        val nm = NotificationManagerCompat.from(this)

        nm.createNotificationChannel(
            NotificationChannelCompat.Builder("foreground_service", NotificationManagerCompat.IMPORTANCE_DEFAULT)
                .setName(resources.getString(R.string.notification__channel_interconnect_name))
                .setDescription(resources.getString(R.string.notification__channel_interconnect_description))
                .build()
        )

        val notification = NotificationCompat.Builder(this, "foreground_service")
            .setContentTitle(resources.getString(R.string.notification_interconnect_description))
            .setSmallIcon(IconCompat.createWithResource(this, R.mipmap.ic_notification_small2))
            .build()

        startForeground(100, notification)
        super.onCreate()
    }

    override fun onBind(intent: Intent): IBinder {
        return InterconnectImpl(this, interconnectHandler)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        stopSelf()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        cancel()

        super.onDestroy()
    }
}
