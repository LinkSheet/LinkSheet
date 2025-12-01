package app.linksheet.feature.app

import android.content.pm.ComponentInfo
import android.os.Parcelable
import fe.android.compose.icon.IconPainter
import fe.composekit.extension.componentName
import fe.kotlin.util.applyIf
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

enum class LinkHandling {
    Browser,
    Allowed,
    Disallowed,
    Unsupported
}

@Parcelize
class DomainVerificationAppInfo(
    val appInfo: AppInfo,
    val linkHandling: LinkHandling,
    val stateNone: MutableList<String>,
    val stateSelected: MutableList<String>,
    val stateVerified: MutableList<String>,
) : Parcelable, IAppInfo by appInfo {

    @IgnoredOnParcel
    val enabled by lazy {
        linkHandling == LinkHandling.Allowed && (stateVerified.isNotEmpty() || stateSelected.isNotEmpty())
    }

    @IgnoredOnParcel
    val hostSum by lazy {
        stateNone.size + stateSelected.size + stateVerified.size
    }

    @IgnoredOnParcel
    val hostSet by lazy {
        (stateNone + stateSelected + stateVerified).toSet()
    }
}

data class ActivityAppInfoStatus(
    val appInfo: ActivityAppInfo,
    val enabled: Boolean,
    val isSourcePackageNameOnly: Boolean
)


@Parcelize
open class ActivityAppInfo(
    val appInfo: AppInfo,
    val componentInfo: @RawValue ComponentInfo,
) : Parcelable, IAppInfo by appInfo {

    @IgnoredOnParcel
    val componentName by lazy { componentInfo.componentName }

    @IgnoredOnParcel
    val flatComponentName by lazy { componentName.flattenToString() }
}


@Parcelize
open class AppInfo(
    override val packageName: String,
    override val label: String,
    @IgnoredOnParcel override val icon: IconPainter? = null,
    override val flags: Int,
    override val installTime: Long? = null,
) : Parcelable, IAppInfo {


}

interface IAppInfo {
    val packageName: String
    val label: String

    @IgnoredOnParcel
    val icon: IconPainter?
    val flags: Int
    val installTime: Long?

    @IgnoredOnParcel
    val compareLabel: String
        get() = label.lowercase()

    fun matches(query: String): Boolean {
        return compareLabel.contains(query, ignoreCase = true) || packageName.contains(
            query,
            ignoreCase = true
        )
    }

    companion object {
        val labelComparator = compareBy<IAppInfo> { it.compareLabel }
    }
}

fun <T : IAppInfo> List<T>.labelSorted(sorted: Boolean = true): List<T> {
    return applyIf(sorted) { sortedWith(IAppInfo.labelComparator) }
}

