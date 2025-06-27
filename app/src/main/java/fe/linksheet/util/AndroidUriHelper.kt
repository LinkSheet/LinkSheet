package fe.linksheet.util

import android.content.Context
import android.net.Uri

object AndroidUriHelper {
    enum class Type(val scheme: String) {
        Package("package"), AppScheme("android-app")
    }

    fun get(type: Type, uri: Uri?): AndroidAppPackage? {
        return if (uri?.scheme == type.scheme) uri.host?.let { AndroidAppPackage(it) } else null
    }

    fun create(type: Type, context: Context): Uri {
        return Uri.fromParts(type.scheme, context.packageName, null)
    }

    fun create(type: Type, packageName: String): Uri {
        return Uri.fromParts(type.scheme, packageName, null)
    }
}

@JvmInline
value class AndroidAppPackage(val packageName: String)

fun AndroidUriHelper.Type.create(packageName: String): Uri {
    return AndroidUriHelper.create(this, packageName)
}
