package fe.linksheet.extension

import android.content.Intent
import android.net.Uri
import com.google.gson.JsonObject
import fe.clearurlskt.ClearURLLoader
import fe.clearurlskt.clearUrl
import fe.fastforwardkt.getRuleRedirect
import timber.log.Timber

fun Intent.newIntent(uri: Uri?, dropExtras: Boolean = false) = Intent(this).apply {
    action = Intent.ACTION_VIEW
    data = uri
    flags = this@newIntent.flags and Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS.inv()
    `package` = null
    component = null

    if (dropExtras) {
        extras?.keySet()?.forEach { extras?.remove(it) }
    }
}

fun Intent.isSchemeTypicallySupportedByBrowsers() = "http" == scheme || "https" == scheme

//{ act=android.intent.action.SEND typ=text/plain flg=0x10800001 cmp=fe.linksheet/.activity.bottomsheet.BottomSheetActivity clip={text/plain {T(59)}} (has extras) }
//{ act=android.intent.action.VIEW dat=https://twitter.com/... flg=0x10800000 cmp=fe.linksheet/.activity.bottomsheet.BottomSheetActivity (has extras) }

private val clearUrlProviders by lazy {
    ClearURLLoader.loadBuiltInClearURLProviders()
}

fun Intent.getUri(
    clearUrl: Boolean = false,
    fastForward: Boolean = false,
    fastForwardRulesObject: JsonObject
): Uri? {
    var uriData = dataString
    if (uriData == null) {
        uriData = getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()
    }

    if (uriData == null) {
        uriData = getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()
    }

    if (uriData != null) {
        val uri = Uri.parse(uriData)
        if (uri.host != null && uri.scheme != null) {
            val host = uri.host!!
            val scheme = "${uri.scheme}://".lowercase()

            var url = buildString {
                append(scheme)
                if (uri.userInfo != null) {
                    append(uri.userInfo).append("@")
                }

                append(host.lowercase())
                append(uriData.substring(uriData.indexOf(host) + host.length))
            }

            Timber.tag("Url Pre modification").d(url)

            if (fastForward) {
                getRuleRedirect(url, fastForwardRulesObject)?.let { url = it }
            }

            Timber.tag("Url Post FastForward").d(url)

            if (clearUrl) {
                url = clearUrl(url, clearUrlProviders)
            }

            Timber.tag("Url Post ClearURL").d(url)
            return Uri.parse(url)
        }
    }

    return null
}

fun Intent.buildSendTo(uri: Uri?): Intent {
    return Intent.createChooser(this.apply {
        this.action = Intent.ACTION_SEND
        this.type = "text/plain"
        this.putExtra(Intent.EXTRA_TEXT, uri?.toString())
    }, null)
}

val allBrowsersIntent = Intent()
    .setAction(Intent.ACTION_VIEW)
    .addCategory(Intent.CATEGORY_BROWSABLE)
    .setData(Uri.fromParts("http", "", ""))