package fe.linksheet.util.extension.android

import android.app.Activity
import android.content.Intent
import fe.std.result.IResult
import fe.std.result.tryCatch

fun Activity.tryStartActivity(intent: Intent): IResult<Unit> {
    return tryCatch { startActivity(intent) }
}
