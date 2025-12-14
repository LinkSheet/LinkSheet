package app.linksheet.api

import android.os.UserHandle

interface RefineWrapper {
    fun <T> unsafeCast(value: Any): T
    fun cast(userHandle: UserHandle): WrappedUserHandleHidden
    fun myUserId(): Int
}

data class WrappedUserHandleHidden(val identifier: Int)
