package fe.linksheet.composable

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.ImageBitmap
import fe.linksheet.extension.android.toImageBitmap

@Stable
open class AppListItemData(
    val packageName: String,
    val label: String,
) {
    val compareLabel = label.lowercase()

    fun matches(query: String): Boolean {
        return compareLabel.contains(query, ignoreCase = true) || packageName.contains(
            query,
            ignoreCase = true
        )
    }

    companion object {
        val labelComparator = compareBy<AppListItemData> { it.compareLabel }
    }

    fun loadIcon(context: Context): ImageBitmap {
        return context.packageManager.getApplicationIcon(packageName).toImageBitmap()
    }
}
