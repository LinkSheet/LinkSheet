package fe.linksheet.module.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.CrossProfileApps
import android.net.Uri
import android.os.*
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.ImageBitmap
import androidx.core.content.getSystemService
import dev.rikka.tools.refine.Refine
import fe.linksheet.R.*
import fe.linksheet.extension.android.toImageBitmap
import fe.linksheet.util.AndroidVersion
import org.koin.dsl.module

val ProfileSwitcherModule = module {
    single<ProfileSwitcher> {
        val context = get<Context>()
        val appLabel = context.resources.getString(string.app_name)
        val crossProfileApps = context.getSystemService<CrossProfileApps>()!!

        ProfileSwitcher(appLabel, crossProfileApps)
    }
}

class ProfileSwitcher(
    private val appLabel: String,
    private val crossProfileApps: CrossProfileApps,
) {

    fun launchCrossProfileInteractSettings(activity: Activity): Boolean {
        if (!AndroidVersion.AT_LEAST_API_30_R) return false

        val intent = crossProfileApps.createRequestInteractAcrossProfilesIntent()
        activity.startActivity(intent)
        return true
    }

    fun needsSetupAtLeastR(): Boolean {
        return if (AndroidVersion.AT_LEAST_API_30_R) needsSetup() else false
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun needsSetup(): Boolean {
        return !crossProfileApps.canInteractAcrossProfiles() && crossProfileApps.canRequestInteractAcrossProfiles()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun switchTo(profile: CrossProfile, url: String, activity: Activity) {
        val switchIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).setComponent(activity.componentName)

        crossProfileApps.startActivity(switchIntent, profile.userHandle, activity)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun startOther(profile: CrossProfile, activity: Activity) {
        crossProfileApps.startMainActivity(activity.componentName, profile.userHandle)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun getProfiles(): List<CrossProfile>? {
        if (!crossProfileApps.canInteractAcrossProfiles()) return null
        return getProfilesInternal()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun getProfilesInternal(): List<CrossProfile>? {
        return crossProfileApps.targetUserProfiles
            .takeIf { it.isNotEmpty() }
            ?.map { toCrossProfile(it) }
    }

    private fun trimLabel(label: String): String {
        return label.indexOf(appLabel).takeIf { it != -1 }?.let { label.substring(0, it).trim() } ?: label
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun toCrossProfile(handle: UserHandle, id: Int): CrossProfile {
        val label = crossProfileApps.getProfileSwitchingLabel(handle).toString()
        val bitmap = crossProfileApps.getProfileSwitchingIconDrawable(handle).toImageBitmap()

        return CrossProfile(handle, id, trimLabel(label), bitmap)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun toCrossProfile(handle: UserHandle): CrossProfile {
        val userHandle = Refine.unsafeCast<UserHandleHidden>(handle)
        return toCrossProfile(handle, userHandle.identifier)
    }
}

data class CrossProfile(
    val userHandle: UserHandle,
    val id: Int,
    val switchLabel: String,
    val bitmap: ImageBitmap,
)
