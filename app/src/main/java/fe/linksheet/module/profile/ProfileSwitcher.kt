package fe.linksheet.module.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.CrossProfileApps
import android.net.Uri
import android.os.Build
import android.os.UserHandle
import android.os.UserHandleHidden
import android.os.UserManager
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.ImageBitmap
import dev.rikka.tools.refine.Refine
import fe.linksheet.R.string
import fe.linksheet.extension.android.toImageBitmap
import fe.linksheet.extension.koin.getSystemServiceOrThrow
import fe.linksheet.util.AndroidVersion
import org.koin.dsl.module

val ProfileSwitcherModule = module {
    single {
        AndroidProfileSwitcherModule(
            appLabel = get<Context>().resources.getString(string.app_name),
            crossProfileApps = getSystemServiceOrThrow(),
            userManager = getSystemServiceOrThrow()
        )
    }
}

internal fun AndroidProfileSwitcherModule(
    appLabel: String,
    crossProfileApps: CrossProfileApps,
    userManager: UserManager
): ProfileSwitcher {
    val isManagedProfile = AndroidVersion.atLeastApi(Build.VERSION_CODES.R) {
        userManager::isManagedProfile
    } ?: { false }

    return RealProfileSwitcher(
        appLabel = appLabel,
        crossProfileApps = crossProfileApps,
        isManagedProfile = isManagedProfile,
        isSystemUser = userManager::isSystemUser,
        getUserProfiles = { userManager.userProfiles },
        getMyUserId = { UserHandleHidden.myUserId() }
    )
}

interface ProfileSwitcher {
    fun checkIsManagedProfile(): Boolean
    fun getStatus(): ProfileStatus
    fun launchCrossProfileInteractSettings(activity: Activity): Boolean
    fun canSetupAtLeastR(): Boolean
    fun canSetup(): Boolean
    fun switchTo(profile: CrossProfile, url: String, activity: Activity)
    fun startOther(profile: CrossProfile, activity: Activity)
    fun getProfiles(): List<CrossProfile>?
    fun getProfilesInternal(): List<CrossProfile>?
}

class RealProfileSwitcher(
    private val appLabel: String,
    private val crossProfileApps: CrossProfileApps,
    private val isManagedProfile: () -> Boolean,
    private val isSystemUser: () -> Boolean,
    private val getUserProfiles: () -> List<UserHandle>,
    private val getMyUserId: () -> Int,
) : ProfileSwitcher {

    override fun checkIsManagedProfile(): Boolean {
        return if (AndroidVersion.AT_LEAST_API_30_R) isManagedProfile()
        else !isSystemUser()
    }

    override fun getStatus(): ProfileStatus = when {
        AndroidVersion.AT_LEAST_API_30_R -> getStatusInternal()
        else -> ProfileStatus.Unsupported
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun getStatusInternal(): ProfileStatus {
        val canInteract = crossProfileApps.canInteractAcrossProfiles()
        val canRequestInteract = crossProfileApps.canRequestInteractAcrossProfiles()

        if (!canInteract && canRequestInteract) {
            return ProfileStatus.NotConnected
        }

        val crossProfiles = getProfilesInternal() ?: emptyList()
        if (crossProfiles.isEmpty()) {
            return ProfileStatus.NoProfiles
        }

        val profiles = getUserProfiles().associateWith { Refine.unsafeCast<UserHandleHidden>(it) }
        val myUserId = getMyUserId()

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

        return ProfileStatus.Available(UserProfileInfo(myUserHandle!!.identifier, otherHandles))
    }

    override fun launchCrossProfileInteractSettings(activity: Activity): Boolean {
        if (!AndroidVersion.AT_LEAST_API_30_R) return false

        val intent = crossProfileApps.createRequestInteractAcrossProfilesIntent()
        activity.startActivity(intent)
        return true
    }

    override fun canSetupAtLeastR(): Boolean {
        return if (AndroidVersion.AT_LEAST_API_30_R) canSetup() else false
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun canSetup(): Boolean {
        return crossProfileApps.targetUserProfiles.isNotEmpty() &&
                !crossProfileApps.canInteractAcrossProfiles() && crossProfileApps.canRequestInteractAcrossProfiles()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun switchTo(profile: CrossProfile, url: String, activity: Activity) {
        val switchIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).setComponent(activity.componentName)

        crossProfileApps.startActivity(switchIntent, profile.userHandle, activity)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun startOther(profile: CrossProfile, activity: Activity) {
        crossProfileApps.startMainActivity(activity.componentName, profile.userHandle)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun getProfiles(): List<CrossProfile>? {
        if (!crossProfileApps.canInteractAcrossProfiles()) return null
        return getProfilesInternal()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun getProfilesInternal(): List<CrossProfile>? {
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

sealed interface ProfileStatus {
    data object Unsupported : ProfileStatus
    data object NoProfiles : ProfileStatus
    data object NotConnected : ProfileStatus
    data class Available(
        val userProfileInfo: UserProfileInfo,
    ) : ProfileStatus
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
