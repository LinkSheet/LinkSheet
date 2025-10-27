package fe.linksheet.util

import android.content.Context
import android.net.Uri

object AndroidUri {
    fun get(scheme: Scheme, uri: Uri?): AndroidAppPackage? {
        return if (uri?.scheme == scheme.scheme) uri.host?.let { AndroidAppPackage(it) } else null
    }

    fun create(scheme: Scheme, context: Context): Uri {
        return Uri.fromParts(scheme.scheme, context.packageName, null)
    }

    fun create(scheme: Scheme, packageName: String): Uri {
        return Uri.fromParts(scheme.scheme, packageName, null)
    }
}

enum class Scheme(val scheme: String) {
    Package("package"),
    AppScheme("android-app")
}

@JvmInline
value class AndroidAppPackage(val packageName: String)

fun Scheme.create(packageName: String): Uri {
    return AndroidUri.create(this, packageName)
}

fun Uri.getAndroidAppPackage(scheme: Scheme): AndroidAppPackage? {
    return AndroidUri.get(scheme, this)
}
