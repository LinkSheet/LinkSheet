package fe.linksheet.module.resolver

import android.content.pm.ComponentInfo
import fe.android.compose.icon.IconPainter
import fe.composekit.extension.componentName

data class DisplayActivityInfo(
//    val resolvedInfo: ResolveInfo? = null,
    val componentInfo: ComponentInfo,
    val label: String,
    val browser: Boolean = false,
    var icon: IconPainter,
) {

    companion object {
        val labelComparator = compareBy<DisplayActivityInfo> { it.compareLabel }
    }

    val compareLabel = label.lowercase()
    val packageName: String = componentInfo.packageName
    val componentName by lazy { componentInfo.componentName }

    override fun equals(other: Any?): Boolean {
        return (other as? DisplayActivityInfo)?.componentName == componentName
    }

    override fun hashCode(): Int = componentName.hashCode()
}
