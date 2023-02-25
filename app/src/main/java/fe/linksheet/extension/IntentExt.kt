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
    var data = dataString
    if (data == null) {
        data = getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()
    }

    if (data == null) {
        data = getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()
    }

    return data?.let { Uri.parse(it.lowercase()) }
}

fun Intent.buildSendTo(uri: Uri): Intent {
    return Intent.createChooser(this.apply {
        this.action = Intent.ACTION_SEND
        this.type = "text/plain"
        this.putExtra(Intent.EXTRA_TEXT, uri.toString())
    }, null)
}