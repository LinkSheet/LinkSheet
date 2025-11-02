package fe.linksheet.module.preference.permission

import android.app.Activity
import androidx.annotation.StringRes

abstract class PermissionBoundPreference(@param:StringRes val title: Int, @param:StringRes val explainer: Int) {
    abstract fun check(): Boolean

    abstract fun request(context: Activity): Boolean
}
