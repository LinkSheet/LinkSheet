package app.linksheet.feature.devicecompat.util

import android.app.AppOpsManager
import android.os.UserHandle
import app.linksheet.api.RefineWrapper
import app.linksheet.api.WrappedAppOpsManagerHidden
import app.linksheet.api.WrappedUserHandleHidden

object RefineWrapperDummy : RefineWrapper {
    override fun <T> unsafeCast(value: Any): T {
        @Suppress("UNCHECKED_CAST")
        return value as T
    }

    override fun cast(userHandle: UserHandle): WrappedUserHandleHidden {
        return WrappedUserHandleHidden(0)
    }

    override fun cast(appOpsManager: AppOpsManager): WrappedAppOpsManagerHidden {
        return WrappedAppOpsManagerHidden(checkOp = { _, _, _ -> 0 })
    }

    override fun myUserId(): Int {
        return 0
    }

    override fun getSystemProperty(key: String): String? {
       return null
    }
}
