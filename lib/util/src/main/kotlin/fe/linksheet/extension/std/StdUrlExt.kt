package fe.linksheet.extension

import android.net.Uri
import fe.std.uri.StdUrl
import androidx.core.net.toUri
import fe.std.uri.toStdUrlOrNull
import fe.std.uri.toStdUrlOrThrow

fun StdUrl.toAndroidUri(): Uri {
    return toString().toUri()
}

fun Uri.toStdUrl(): StdUrl? {
    return toString().toStdUrlOrNull()
}

fun Uri.toStdUrlOrThrow(): StdUrl {
    return toString().toStdUrlOrThrow()
}
