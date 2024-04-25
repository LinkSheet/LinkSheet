package fe.linksheet.util

import android.content.pm.ResolveInfo
import fe.kotlin.extension.iterable.combineEachTo


class LinkSheetPackage private constructor(
    private val basePackage: String,
    private val flavors: Set<String?>,
    private val buildTypes: Set<String?>,
) {
    private val String?.asPackagePart: String
        get() = if (this == null) "" else ".$this"

    val allPackages by lazy {
        flavors.combineEachTo(
            mutableSetOf(),
            buildTypes
        ) { flavor, buildType -> "$basePackage${flavor.asPackagePart}${buildType.asPackagePart}" }
    }

    fun isPackage(resolveInfo: ResolveInfo): Boolean {
        return resolveInfo.activityInfo.packageName in allPackages
    }

    fun isPackage(`package`: String): Boolean {
        return `package` in allPackages
    }

    companion object {
        val App = LinkSheetPackage("fe.linksheet", setOf(null, "pro"), setOf(null, "debug", "nightly"))
        val Compat = LinkSheetPackage("fe.linksheet.compat", setOf(null), setOf(null, "debug"))

        @Deprecated(
            message = "Replace with `is`",
            replaceWith = ReplaceWith("is(resolveInfo, LinkSheetPackage.Compat)")
        )
        fun isCompat(resolveInfo: ResolveInfo): Boolean {
            return `is`(resolveInfo.activityInfo.packageName, Compat)
        }

        @Deprecated(message = "Replace with `is`", replaceWith = ReplaceWith("is(pkg, LinkSheetPackage.Compat)"))
        fun isCompat(pkg: String): Boolean {
            return `is`(pkg, Compat)
        }

        fun `is`(resolveInfo: ResolveInfo, type: LinkSheetPackage): Boolean {
            return type.isPackage(resolveInfo)
        }

        fun `is`(`package`: String, type: LinkSheetPackage): Boolean {
            return `package` in type.allPackages
        }
    }
}
