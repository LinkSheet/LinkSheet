package fe.linksheet.experiment.ui.overhaul.composable

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.ImageBitmap
import fe.linksheet.extension.android.toImageBitmap

@Stable
open class AppListItemData(
    val applicationInfo: ApplicationInfo,
    val label: String,
) {
    val compareLabel = label.lowercase()
    val packageName: String = applicationInfo.packageName

    fun matches(query: String): Boolean {
        return compareLabel.contains(query, ignoreCase = true) || applicationInfo.packageName.contains(
            query,
            ignoreCase = true
        )
    }

    companion object {
        val labelComparator = compareBy<AppListItemData> { it.compareLabel }
    }

    fun loadIcon(context: Context): ImageBitmap {
        return applicationInfo.loadIcon(context.packageManager).toImageBitmap()
    }
}
