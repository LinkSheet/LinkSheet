package fe.linksheet.module.profile

import android.app.Activity
import android.content.Intent
import android.os.UserHandle
import android.os.UserHandleHidden
import android.os.UserManager
import androidx.compose.ui.graphics.ImageBitmap
import dev.rikka.tools.refine.Refine
import fe.android.version.AndroidVersion
import fe.linksheet.extension.android.toImageBitmap
import androidx.core.net.toUri

@Suppress("FunctionName")
internal fun AndroidProfileSwitcher(
    appLabel: String,
    crossProfileAppsCompat: CrossProfileAppsCompat,
    userManager: UserManager
): ProfileSwitcher {
    val userManagerCompat = UserManagerCompat(userManager)

    return RealProfileSwitcher(
        appLabel = appLabel,
        crossProfileAppsCompat = crossProfileAppsCompat,
        userManagerCompat = userManagerCompat,
    )
}

interface ProfileSwitcher {
    fun checkIsManagedProfile(): Boolean
    fun getStatus(): ProfileStatus
    fun getUserProfileInfo(status: ProfileStatus = getStatus()): UserProfileInfo?
    fun launchCrossProfileInteractSettings(activity: Activity): Boolean
    fun canQuickToggle(): Boolean
    fun switchTo(profile: CrossProfile, url: String, activity: Activity)
    fun startOther(profile: CrossProfile, activity: Activity)
    fun getProfiles(status: ProfileStatus = getStatus()): List<CrossProfile>?
}

internal class RealProfileSwitcher(
    private val appLabel: String,
    private val crossProfileAppsCompat: CrossProfileAppsCompat,
    private val userManagerCompat: UserManagerCompat,
) : ProfileSwitcher {

    override fun checkIsManagedProfile(): Boolean {
        return userManagerCompat.isManagedProfile()
    }

    override fun getStatus(): ProfileStatus = when {
        AndroidVersion.isAtLeastApi30R() -> getStatusFast()
        else -> ProfileStatus.Unsupported
    }

    private fun getStatusFast(): ProfileStatus {
        val canInteract = crossProfileAppsCompat.canInteractAcrossProfiles()
        val canRequestInteract = crossProfileAppsCompat.canRequestInteractAcrossProfiles()

        if (!canInteract && canRequestInteract) {
            return ProfileStatus.NotConnected
        }

        val targetUserProfiles = crossProfileAppsCompat.getTargetUserProfiles()
        if (targetUserProfiles.isEmpty()) {
            return ProfileStatus.NoProfiles
        }

        return ProfileStatus.Available(targetUserProfiles)
    }

    override fun getUserProfileInfo(status: ProfileStatus): UserProfileInfo? {
        val crossProfiles = getProfiles(status)
        if (crossProfiles == null) return null

        val profiles = userManagerCompat.getUserProfiles().associateWith { Refine.unsafeCast<UserHandleHidden>(it) }
        val myUserId = userManagerCompat.getMyUserId()

        var myUserHandle: UserHandleHidden? = null
        val otherHandles = mutableListOf<Pair<Int, CrossProfile?>>()

        for ((_, hiddenUserHandle) in profiles) {
            if (hiddenUserHandle.identifier == myUserId) {
                myUserHandle = hiddenUserHandle
            } else {
                val crossProfile = crossProfiles.firstOrNull { it.id == hiddenUserHandle.identifier }
                otherHandles.add(hiddenUserHandle.identifier to crossProfile)
            }
        }

        return UserProfileInfo(myUserHandle!!.identifier, otherHandles)
    }

    override fun launchCrossProfileInteractSettings(activity: Activity): Boolean {
        if (!AndroidVersion.isAtLeastApi30R()) return false

        val intent = crossProfileAppsCompat.createRequestInteractAcrossProfilesIntent() ?: return false
        activity.startActivity(intent)
        return true
    }

    override fun canQuickToggle(): Boolean {
        val status = getStatusFast()
        return status is ProfileStatus.Available
    }

    override fun switchTo(profile: CrossProfile, url: String, activity: Activity) {
        val switchIntent = Intent(Intent.ACTION_VIEW, url.toUri())
            .setComponent(activity.componentName)
        crossProfileAppsCompat.startActivity(switchIntent, profile.userHandle, activity)
    }

    override fun startOther(profile: CrossProfile, activity: Activity) {
        crossProfileAppsCompat.startMainActivity(activity.componentName, profile.userHandle)
    }

    override fun getProfiles(status: ProfileStatus): List<CrossProfile>? {
        if (status !is ProfileStatus.Available) return null
        return status.targetUserProfiles.mapNotNull(::toCrossProfile)
    }

    private fun trimLabel(label: String): String {
        return label.indexOf(appLabel)
            .takeIf { it != -1 }
            ?.let { label.substring(0, it).trim() }
            ?: label
    }

    private fun toCrossProfile(handle: UserHandle, id: Int): CrossProfile? {
        val info = crossProfileAppsCompat.getProfileInfo(handle)
        if (info is RealProfileInfoCompat) {
            return CrossProfile(handle, id, trimLabel(info.label), info.drawable.toImageBitmap())
        }

        return null
    }

    private fun toCrossProfile(handle: UserHandle): CrossProfile? {
        val userHandle = Refine.unsafeCast<UserHandleHidden>(handle)
        return toCrossProfile(handle, userHandle.identifier)
    }
}

sealed interface ProfileStatus {
    data object Unsupported : ProfileStatus
    data object NoProfiles : ProfileStatus
    data object NotConnected : ProfileStatus
    data class Available(val targetUserProfiles: List<UserHandle>) : ProfileStatus
}

data class UserProfileInfo(
    val userHandle: Int,
    val otherHandles: List<Pair<Int, CrossProfile?>>
)

data class CrossProfile(
    val userHandle: UserHandle,
    val id: Int,
    val switchLabel: String,
    val bitmap: ImageBitmap,
)
