package com.tasomaniac.openwith.resolver

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Parcelable
import com.tasomaniac.openwith.data.PreferredApp
import fe.linksheet.extension.componentName
import fe.linksheet.extension.toImageBitmap
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

data class DisplayActivityInfo(
    val activityInfo: ActivityInfo,
    val label: String,
    val extendedInfo: CharSequence? = null,
    val icon: Drawable? = null,
    val resolvedInfo: ResolveInfo,
) {
    companion object {
        val labelComparator = compareBy<DisplayActivityInfo> { it.compareLabel }
        private val valueAndLabelComparator = compareByDescending<Pair<DisplayActivityInfo, Boolean>> { (_, bool) ->
            bool
        }.thenBy { (activityInfo, _) -> activityInfo.compareLabel }

        fun List<Pair<DisplayActivityInfo, Boolean>>.sortByValueAndName() = sortedWith(valueAndLabelComparator)
    }

    val compareLabel = label.lowercase()
    val packageName: String = activityInfo.packageName
    val componentName by lazy { activityInfo.componentName() }
    val flatComponentName by lazy { componentName.flattenToString() }
    val iconBitmap by lazy { icon!!.toImageBitmap() }

    fun intentFrom(sourceIntent: Intent): Intent {
        return Intent(sourceIntent)
            .setComponent(componentName)
            .addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT or Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP)
    }

    fun toPreferredApp(
        host: String,
        alwaysPreferred: Boolean
    ) = PreferredApp(
        host = host,
        packageName = packageName,
        component = componentName.flattenToString(),
        alwaysPreferred = alwaysPreferred
    )

    override fun equals(other: Any?): Boolean {
        return (other as? DisplayActivityInfo)?.componentName == componentName
    }

    override fun hashCode(): Int = componentName.hashCode()
}
