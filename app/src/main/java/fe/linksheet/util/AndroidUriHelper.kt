package fe.linksheet.util

import android.content.Context
import android.net.Uri

object AndroidUriHelper {
    enum class Type(val scheme: String) {
        Package("package"), AppScheme("android-app")
    }

    fun get(type: Type, uri: Uri?): String? {
        return if (uri?.scheme == type.scheme) uri.host else null
    }

    fun create(type: Type, context: Context): Uri {
        return Uri.fromParts(type.scheme, context.packageName, null)
    }

    fun create(type: Type, packageName: String): Uri {
        return Uri.fromParts(type.scheme, packageName, null)
    }
}
