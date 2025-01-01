package fe.linksheet.module.app

import android.content.Context
import android.content.pm.ActivityInfo
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.ImageBitmap
import fe.kotlin.util.applyIf
import fe.linksheet.extension.android.componentName
import fe.linksheet.extension.android.toImageBitmap


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

@Stable
open class ActivityAppInfo(
    val activityInfo: ActivityInfo,
    label: String,
    icon: Lazy<ImageBitmap>? = null,
) : AppInfo(activityInfo.packageName, label, icon) {

    val componentName by lazy { activityInfo.componentName() }
    val flatComponentName by lazy { componentName.flattenToString() }

    companion object {
        val labelComparator = compareBy<ActivityAppInfo> { it.compareLabel }
    }
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
