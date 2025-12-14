package app.linksheet.api

import android.app.AppOpsManager
import android.os.UserHandle

interface RefineWrapper {
    fun <T> unsafeCast(value: Any): T
    fun cast(userHandle: UserHandle): WrappedUserHandleHidden
    fun cast(appOpsManager: AppOpsManager): WrappedAppOpsManagerHidden
    fun myUserId(): Int
}

data class WrappedUserHandleHidden(val identifier: Int)
data class WrappedAppOpsManagerHidden(val checkOp: (op: Int, uid: Int, packageName: String) -> Int)
