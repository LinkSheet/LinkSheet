package fe.linksheet.util

import android.content.Context

interface StringResHolder {
    fun stringResId(): Int

    fun args(): Array<out Any> {
        return emptyArray()
    }

    fun getString(context: Context): String {
        return context.getString(stringResId(), *args())
    }
}
