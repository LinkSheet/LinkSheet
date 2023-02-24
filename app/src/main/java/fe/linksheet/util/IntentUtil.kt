package fe.linksheet.util

import android.content.Intent
import fe.linksheet.extension.getUri

fun Intent.sourceIntent() = Intent(this).apply {
    component = null
    action = Intent.ACTION_VIEW
    data = this.getUri()
    flags = flags and Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS.inv()
}

//{ act=android.intent.action.SEND typ=text/plain flg=0x10800001 cmp=fe.linksheet/.activity.bottomsheet.BottomSheetActivity clip={text/plain {T(59)}} (has extras) }
//{ act=android.intent.action.VIEW dat=https://twitter.com/... flg=0x10800000 cmp=fe.linksheet/.activity.bottomsheet.BottomSheetActivity (has extras) }