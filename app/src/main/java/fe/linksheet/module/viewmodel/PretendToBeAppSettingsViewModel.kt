package fe.linksheet.module.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.content.pm.verify.domain.DomainVerificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import fe.linksheet.module.preference.app.AppPreferenceRepository

import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.composekit.core.AndroidVersion
import fe.linksheet.util.getAppOpenByDefaultIntent

class PretendToBeAppSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {
    companion object {
        val linksheetCompatPackage = "fe.linksheet.compat"
        val compatComponentName =
            ComponentName(linksheetCompatPackage, "$linksheetCompatPackage.MainActivity")
    }

    private val domainVerificationManager by lazy {
        if (AndroidVersion.isAtLeastApi31S()) {
            context.getSystemService<DomainVerificationManager>()
        } else null
    }

    fun checkIsCompatInstalled(): Boolean {
        return runCatching {
            context.packageManager.getPackageInfo(
                linksheetCompatPackage,
                0
            ) != null
        }.getOrDefault(false)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun configureCompatIntent(): Intent {
       return getAppOpenByDefaultIntent(linksheetCompatPackage)
    }
}
