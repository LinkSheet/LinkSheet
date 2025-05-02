package fe.linksheet.module.resolver.util

import android.content.Context
import android.net.Uri

object ReferrerHelper {
    private const val APP_SCHEME: String = "android-app"

    fun getReferringPackage(uri: Uri?): AndroidAppPackage? {
        return if (uri?.scheme == APP_SCHEME) uri.host?.let { AndroidAppPackage(it) }
        else null
    }

    fun createReferrer(context: Context): Uri {
        return Uri.fromParts(APP_SCHEME, context.packageName, null)
    }

    fun createReferrer(packageName: String): Uri {
        return Uri.fromParts(APP_SCHEME, packageName, null)
    }
}

@JvmInline
value class AndroidAppPackage(val packageName: String)
