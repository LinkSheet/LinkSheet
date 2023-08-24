package fe.linksheet

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.core.graphics.drawable.IconCompat
import fe.linksheet.activity.SelectDomainsConfirmationActivity
import fe.linksheet.interconnect.ILinkSheetService
import fe.linksheet.interconnect.StringParceledListSlice
import fe.linksheet.module.repository.PreferredAppRepository
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject

class InterconnectService : Service() {
    private val repository: PreferredAppRepository by inject()

    override fun onCreate() {
        val nm = NotificationManagerCompat.from(this)

        nm.createNotificationChannel(
            NotificationChannelCompat.Builder("foreground_service", NotificationManagerCompat.IMPORTANCE_DEFAULT)
                .setName(resources.getString(R.string.app_name))
                .build()
        )

        val notification = NotificationCompat.Builder(this, "foreground_service")
            .setContentTitle(resources.getString(R.string.app_name))
            .setSmallIcon(IconCompat.createWithResource(this, R.mipmap.ic_launcher_foreground))
            .build()

        startForeground(100, notification)

        super.onCreate()
    }

    override fun onBind(intent: Intent): IBinder {
        return object : ILinkSheetService.Stub() {
            override fun getSelectedDomains(packageName: String): StringParceledListSlice {
                verifyCaller(packageName)

                return runBlocking {
                    StringParceledListSlice(repository.getByPackageName(packageName).map { it.host })
                }
            }

            override fun selectDomains(
                packageName: String,
                domains: StringParceledListSlice,
                componentName: ComponentName,
            ) {
                verifyCaller(packageName)

                SelectDomainsConfirmationActivity.start(
                    this@InterconnectService, packageName, componentName, domains,
                )
            }
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        stopSelf()

        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)

        super.onDestroy()
    }

    private fun verifyCaller(passedPackage: String) {
        val callingPackages = packageManager.getPackagesForUid(Binder.getCallingUid())
        if (callingPackages?.contains(passedPackage) != true) {
            throw IllegalAccessException("Calling package is not $passedPackage!")
        }
    }
}