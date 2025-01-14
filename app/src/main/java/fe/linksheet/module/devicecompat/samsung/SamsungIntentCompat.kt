package fe.linksheet.module.devicecompat.samsung

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import fe.linksheet.module.systeminfo.SystemInfoService
import fe.std.lazy.ResettableLazy
import fe.std.lazy.resettableLazy

interface SamsungIntentCompatProvider {
    val isRequired: ResettableLazy<Boolean>

    fun provideCompat(context: Context): SamsungIntentCompat
}

class RealSamsungIntentCompatProvider(
    val infoService: SystemInfoService,
) : SamsungIntentCompatProvider {

    override val isRequired = resettableLazy {
        with(infoService.build) {
            manufacturer.contains("samsung", ignoreCase = true) && sdk == Build.VERSION_CODES.S
        }
    }

    override fun provideCompat(context: Context): SamsungIntentCompat = when {
        !isRequired.value -> DefaultSamsungIntentCompat
        else -> RealSamsungIntentCompat
    }
}

interface SamsungIntentCompat {
    fun createAppOpenByDefaultSettingsIntent(packageName: String): Intent
}

object DefaultSamsungIntentCompat : SamsungIntentCompat {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun createAppOpenByDefaultSettingsIntent(packageName: String): Intent {
        return Intent(
            Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
    }
}

object RealSamsungIntentCompat : SamsungIntentCompat {
    // Intent is broken on Samsung A12 (https://stackoverflow.com/a/72365164)
    private val intent = Intent("android.settings.MANAGE_DOMAIN_URLS")

    override fun createAppOpenByDefaultSettingsIntent(packageName: String): Intent {
        return intent
    }
}
