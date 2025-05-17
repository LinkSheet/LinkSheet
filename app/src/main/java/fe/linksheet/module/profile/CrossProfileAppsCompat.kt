package fe.linksheet.module.profile

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.CrossProfileApps
import android.graphics.drawable.Drawable
import android.os.UserHandle
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import fe.composekit.core.AndroidVersion

interface CrossProfileAppsCompat {
    fun getProfileInfo(userHandle: UserHandle): ProfileInfoCompat = UnsupportedProfileInfoCompat
    fun getTargetUserProfiles(): List<UserHandle> = emptyList()
    fun canInteractAcrossProfiles(): Boolean = false
    fun canRequestInteractAcrossProfiles(): Boolean = false
    fun createRequestInteractAcrossProfilesIntent(): Intent? = null
    fun startMainActivity(component: ComponentName, targetUser: UserHandle): Boolean = false
    fun startActivity(switchIntent: Intent, userHandle: UserHandle, activity: Activity): Boolean = false
}

fun CrossProfileAppsCompat(context: Context) = when {
    AndroidVersion.isAtLeastApi30R() -> CrossProfileAppsCompatImpl.Api30(context.getSystemService<CrossProfileApps>()!!)
    AndroidVersion.isAtLeastApi28P() -> CrossProfileAppsCompatImpl.Api28(context.getSystemService<CrossProfileApps>()!!)
    else -> CrossProfileAppsCompatImpl.PreApi28
}

private object CrossProfileAppsCompatImpl {
    object PreApi28 : CrossProfileAppsCompat

    @RequiresApi(28)
    open class Api28(val crossProfileApps: CrossProfileApps) : CrossProfileAppsCompat {

        override fun getProfileInfo(userHandle: UserHandle): ProfileInfoCompat {
            return RealProfileInfoCompat(
                crossProfileApps.getProfileSwitchingLabel(userHandle).toString(),
                crossProfileApps.getProfileSwitchingIconDrawable(userHandle)
            )
        }

        override fun getTargetUserProfiles(): List<UserHandle> {
            return crossProfileApps.targetUserProfiles
        }

        override fun startMainActivity(
            component: ComponentName,
            targetUser: UserHandle
        ): Boolean {
            crossProfileApps.startMainActivity(component, targetUser)
            return true
        }
    }

    @RequiresApi(30)
    class Api30(crossProfileApps: CrossProfileApps) : Api28(crossProfileApps) {

        override fun canInteractAcrossProfiles(): Boolean {
            return crossProfileApps.canInteractAcrossProfiles()
        }

        override fun canRequestInteractAcrossProfiles(): Boolean {
            return crossProfileApps.canRequestInteractAcrossProfiles()
        }

        override fun createRequestInteractAcrossProfilesIntent(): Intent? {
            return crossProfileApps.createRequestInteractAcrossProfilesIntent()
        }

        override fun startActivity(intent: Intent, userHandle: UserHandle, activity: Activity): Boolean {
            crossProfileApps.startActivity(intent, userHandle, activity)
            return true
        }
    }
}

sealed interface ProfileInfoCompat
data class RealProfileInfoCompat(val label: String, val drawable: Drawable) : ProfileInfoCompat
data object UnsupportedProfileInfoCompat : ProfileInfoCompat
