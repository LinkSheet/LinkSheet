package fe.linksheet.module.app

import android.content.Context
import android.content.pm.ComponentInfo
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.ImageBitmap
import fe.kotlin.util.applyIf
import fe.linksheet.extension.android.componentName
import fe.linksheet.extension.android.toImageBitmap
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.resolver.DisplayActivityInfo


@Stable
class DomainVerificationAppInfo(
    packageName: String,
    label: CharSequence,
    val flags: Int,
    val isLinkHandlingAllowed: Boolean,
    val stateNone: MutableList<String>,
    val stateSelected: MutableList<String>,
    val stateVerified: MutableList<String>,
) : AppInfo(packageName, label.toString()) {

    val enabled = isLinkHandlingAllowed && (stateVerified.isNotEmpty() || stateSelected.isNotEmpty())
    val hostSum = stateNone.size + stateSelected.size + stateVerified.size
//        val disabled = stateNone.isNotEmpty()
}

typealias ActivityAppInfoStatus = Pair<ActivityAppInfo, Boolean>

object ActivityAppInfoSortCompat {
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

object ActivityAppInfoCompat {
    fun toDisplayActivityInfo(appInfo: ActivityAppInfo): DisplayActivityInfo {
        return DisplayActivityInfo(
            componentInfo = appInfo.componentInfo,
            label = appInfo.label,
            browser = false,
            icon = appInfo.icon!!
        )
    }
}

@Stable
open class ActivityAppInfo(
    val componentInfo: ComponentInfo,
    label: String,
    icon: Lazy<ImageBitmap>? = null,
) : AppInfo(componentInfo.packageName, label, icon) {

    val componentName by lazy { componentInfo.componentName }
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

@Stable
open class AppInfo(
    val packageName: String,
    val label: String,
    val icon: Lazy<ImageBitmap>? = null,
) {
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

    fun loadIcon(context: Context): ImageBitmap {
        return context.packageManager.getApplicationIcon(packageName).toImageBitmap()
    }
}

fun <T : AppInfo> List<T>.labelSorted(sorted: Boolean = true): List<T> {
    return applyIf(sorted) { sortedWith(AppInfo.labelComparator) }
}
