package fe.linksheet.extension

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent

fun Context.startActivityWithConfirmation(intent: Intent): Boolean {
    return try {
        this.startActivity(intent)
        true
    } catch (e: ActivityNotFoundException) {
        false
    }

}