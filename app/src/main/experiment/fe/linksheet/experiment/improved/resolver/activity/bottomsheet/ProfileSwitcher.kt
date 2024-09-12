package fe.linksheet.experiment.improved.resolver.activity.bottomsheet

import android.app.Activity
import android.content.Intent
import android.content.pm.CrossProfileApps
import android.net.Uri
import android.os.Build
import android.os.UserHandle
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.ImageBitmap
import fe.linksheet.extension.android.toImageBitmap

class ProfileSwitcher(
    private val appLabel: String,
    private val crossProfileApps: CrossProfileApps,
) {

    fun switchTo(profile: CrossProfile, uri: Uri, activity: Activity) {
        val switchIntent = Intent(Intent.ACTION_VIEW, uri).setComponent(activity.componentName)

        crossProfileApps.startActivity(switchIntent, profile.userHandle, activity)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun getProfiles(): List<CrossProfile>? {
        if (!crossProfileApps.canInteractAcrossProfiles()) return null

        return crossProfileApps.targetUserProfiles
            .takeIf { it.isNotEmpty() }
            ?.map { toCrossProfile(it) }
    }

    private fun trimLabel(label: String): String {
        return label.indexOf(appLabel).takeIf { it != -1 }?.let { label.substring(0, it).trim() } ?: label
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun toCrossProfile(target: UserHandle): CrossProfile {
        val label = crossProfileApps.getProfileSwitchingLabel(target).toString()
        val bitmap = crossProfileApps.getProfileSwitchingIconDrawable(target).toImageBitmap()

        return CrossProfile(target, trimLabel(label), bitmap)
    }
}

data class CrossProfile(val userHandle: UserHandle, val label: String, val bitmap: ImageBitmap)
