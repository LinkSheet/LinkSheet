package app.linksheet.feature.devicecompat.oneui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import fe.composekit.intent.buildIntent
import fe.linksheet.feature.systeminfo.SystemInfoService
import fe.linksheet.util.Scheme
import fe.linksheet.util.create
import fe.std.lazy.ResettableLazy
import fe.std.lazy.resettableLazy

interface OneUiCompatProvider {
    val isSamsungDevice: Boolean
    fun readOneUiVersion(): Int?

    val isRequired: ResettableLazy<Boolean>

    fun provideCompat(context: Context): OneUiCompat
}

class RealOneUiCompatProvider(
    private val infoService: SystemInfoService,
) : OneUiCompatProvider {

    override val isSamsungDevice: Boolean = infoService.build.manufacturer.contains("samsung", ignoreCase = true)

    override fun readOneUiVersion(): Int? {
        val result = runCatching { infoService.properties.get("ro.build.version.oneui") }
        return result.map { it?.toIntOrNull() }.getOrNull()
    }

    override val isRequired = resettableLazy {
        isSamsungDevice && !infoService.isCustomRom && infoService.build.sdk == Build.VERSION_CODES.S && readOneUiVersion() != null
    }

    override fun provideCompat(context: Context): OneUiCompat = when {
        !isRequired.value -> DefaultOneUiIntentCompat
        else -> RealOneUiIntentCompat
    }
}

interface OneUiCompat {
    fun createAppOpenByDefaultSettingsIntent(packageName: String): Intent
}

object DefaultOneUiIntentCompat : OneUiCompat {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun createAppOpenByDefaultSettingsIntent(packageName: String): Intent {
        return buildIntent(
            Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS, Scheme.Package.create(packageName)
        )
    }
}

object RealOneUiIntentCompat : OneUiCompat {
    // Intent is broken on Samsung A12 (https://stackoverflow.com/a/72365164)
    private val intent = Intent("android.settings.MANAGE_DOMAIN_URLS")

    override fun createAppOpenByDefaultSettingsIntent(packageName: String): Intent {
        return intent
    }
}
