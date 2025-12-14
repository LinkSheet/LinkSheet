package app.linksheet.feature.profile.core

import android.os.UserHandle
import android.os.UserManager
import fe.composekit.core.AndroidVersion

interface UserManagerCompat {
    fun isManagedProfile(): Boolean = false
    fun getUserProfiles(): List<UserHandle>
    fun getMyUserId(): Int = 0
}

internal class UserManagerCompatImpl(
    private val userManager: UserManager,
    private val myUserId: Int
) : UserManagerCompat {
    override fun isManagedProfile(): Boolean {
        if (AndroidVersion.isAtLeastApi30R()) {
            return userManager.isManagedProfile
        }

        return !userManager.isSystemUser
    }

    override fun getMyUserId(): Int {
        return myUserId
    }

    override fun getUserProfiles(): List<UserHandle> {
        return userManager.userProfiles
    }
}
