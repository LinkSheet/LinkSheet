package fe.linksheet.module.preference.permission

import android.app.Activity
import android.content.Context
import androidx.annotation.StringRes

abstract class PermissionBoundPreference(@StringRes val title: Int, @StringRes val explainer: Int) {
    abstract fun check(): Boolean

    abstract fun request(context: Activity): Boolean
}
