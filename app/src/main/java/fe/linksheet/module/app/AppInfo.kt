package fe.linksheet.module.app

import android.content.pm.ComponentInfo
import android.os.Parcelable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.ImageBitmap
import fe.kotlin.util.applyIf
import fe.linksheet.extension.android.componentName
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.resolver.DisplayActivityInfo
import fe.linksheet.util.RefactorGlue
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
@Stable
class DomainVerificationAppInfo(
    packageName: String,
    label: CharSequence,
    icon: Lazy<ImageBitmap>? = null,
    val flags: Int,
    val linkHandling: LinkHandling,
    val stateNone: MutableList<String>,
    val stateSelected: MutableList<String>,
    val stateVerified: MutableList<String>,
) : AppInfo(packageName, label.toString(), icon) {

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

typealias ActivityAppInfoStatus = Pair<ActivityAppInfo, Boolean>

@RefactorGlue(reason = "Ensure compatibility of (new) PackageService with old UI")
object ActivityAppInfoSortGlue {
    private val valueAndLabelComparator = compareByDescending<ActivityAppInfoStatus> { (_, status) ->
        status
    }.thenBy { (activityInfo, _) -> activityInfo.compareLabel }

    private fun mapBrowserState(appInfo: ActivityAppInfo, pkgs: Set<String>): ActivityAppInfoStatus {
        return appInfo to (appInfo.packageName in pkgs)
    }

    fun mapBrowserState(browsers: List<ActivityAppInfo>, pkgs: Set<String>): Map<ActivityAppInfo, Boolean> {
        return browsers.map { mapBrowserState(it, pkgs) }.sortedWith(valueAndLabelComparator).toMap()
    }
}

@RefactorGlue(reason = "Ensure compatibility of (new) PackageService with old UI")
object ActivityAppInfoGlue {
    fun toDisplayActivityInfo(appInfo: ActivityAppInfo): DisplayActivityInfo {
        return DisplayActivityInfo(
            componentInfo = appInfo.componentInfo,
            label = appInfo.label,
            browser = false,
            icon = appInfo.icon!!
        )
    }
}

@Parcelize
@Stable
open class ActivityAppInfo(
    val componentInfo: @RawValue ComponentInfo,
    label: String,
    icon: Lazy<ImageBitmap>? = null,
) : AppInfo(componentInfo.packageName, label, icon) {

    @IgnoredOnParcel
    val componentName by lazy { componentInfo.componentName }

    @IgnoredOnParcel
    val flatComponentName by lazy { componentName.flattenToString() }

    companion object {
        val labelComparator = compareBy<ActivityAppInfo> { it.compareLabel }
    }
}

fun ActivityAppInfo.toPreferredApp(host: String, alwaysPreferred: Boolean): PreferredApp {
    return PreferredApp.new(
        host = host,
        pkg = packageName,
        cmp = componentName,
        always = alwaysPreferred
    )
}

@Parcelize
@Stable
open class AppInfo(
    val packageName: String,
    val label: String,
    @IgnoredOnParcel val icon: Lazy<ImageBitmap>? = null,
) : Parcelable {

    @IgnoredOnParcel
    val compareLabel = label.lowercase()

    fun matches(query: String): Boolean {
        return compareLabel.contains(query, ignoreCase = true) || packageName.contains(
            query,
            ignoreCase = true
        )
    }

    companion object {
        val labelComparator = compareBy<AppInfo> { it.compareLabel }
    }
}

fun <T : AppInfo> List<T>.labelSorted(sorted: Boolean = true): List<T> {
    return applyIf(sorted) { sortedWith(AppInfo.labelComparator) }
}

fun AppInfo.toPreferredApp(host: String, alwaysPreferred: Boolean): PreferredApp {
    return PreferredApp.new(
        host = host,
        pkg = packageName,
        cmp = null,
        always = alwaysPreferred
    )
}
