package fe.linksheet.extension

import android.content.Intent
import android.net.Uri

fun Intent.getUri(): Uri? {
    var uri = data
    if(uri == null){
        uri = Uri.parse(getCharSequenceExtra(Intent.EXTRA_TEXT).toString())
    }

    if(uri == null){
        uri = Uri.parse(getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString())
    }

    return uri
}