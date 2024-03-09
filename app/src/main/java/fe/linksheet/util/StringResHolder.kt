package fe.linksheet.util

import android.content.Context
import androidx.annotation.StringRes

interface StringResHolder {
    @get:StringRes
    val id: Int

    val args: Array<Any>
        get() = emptyArray()

    fun getString(context: Context): String {
        return context.getString(id, *args)
    }
}
