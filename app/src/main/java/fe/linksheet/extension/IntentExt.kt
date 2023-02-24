package fe.linksheet.extension

import android.content.Intent
import android.net.Uri

fun Intent.sourceIntent() = Intent(this).apply {
    component = null
    action = Intent.ACTION_VIEW
    data = this.getUri()
    flags = flags and Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS.inv()
}

//{ act=android.intent.action.SEND typ=text/plain flg=0x10800001 cmp=fe.linksheet/.activity.bottomsheet.BottomSheetActivity clip={text/plain {T(59)}} (has extras) }
//{ act=android.intent.action.VIEW dat=https://twitter.com/... flg=0x10800000 cmp=fe.linksheet/.activity.bottomsheet.BottomSheetActivity (has extras) }

fun Intent.getUri(): Uri? {
    var uri = data
    if (uri == null) {
        uri = Uri.parse(getCharSequenceExtra(Intent.EXTRA_TEXT).toString())
    }

    if (uri == null) {
        uri = Uri.parse(getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString())
    }

    return uri
}

fun Intent.buildSendTo(uri: Uri): Intent {
    return this.apply {
        this.action = Intent.ACTION_SEND
        this.type = "text/plain"
        this.putExtra(Intent.EXTRA_TEXT, uri.toString())
    }
}