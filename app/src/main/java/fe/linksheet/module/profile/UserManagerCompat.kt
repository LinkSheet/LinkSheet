package fe.linksheet.module.profile

import android.os.UserHandle
import android.os.UserHandleHidden
import android.os.UserManager
import fe.android.compose.version.AndroidVersion

interface UserManagerCompat {
    fun isManagedProfile(): Boolean = false
    fun getUserProfiles(): List<UserHandle>
    fun getMyUserId(): Int = 0
}

fun UserManagerCompat(userManager: UserManager): UserManagerCompat {
    return UserManagerCompatImpl(userManager)
}

private class UserManagerCompatImpl(val userManager: UserManager) : UserManagerCompat {
    override fun isManagedProfile(): Boolean {
        if (AndroidVersion.AT_LEAST_API_30_R) {
            return userManager.isManagedProfile
        }

        return !userManager.isSystemUser
    }

    override fun getMyUserId(): Int {
        return UserHandleHidden.myUserId()
    }

    override fun getUserProfiles(): List<UserHandle> {
        return userManager.userProfiles
    }
}
