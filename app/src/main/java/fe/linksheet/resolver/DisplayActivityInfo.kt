package fe.linksheet.resolver

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import fe.linksheet.extension.android.componentName
import fe.linksheet.extension.android.toImageBitmap
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.log.hasher.HashProcessor
import fe.linksheet.module.log.hasher.LogDumpable
import fe.linksheet.module.log.hasher.LogHasher
import fe.stringbuilder.util.commaSeparated

typealias DisplayActivityInfoStatus = Pair<DisplayActivityInfo, Boolean>

data class DisplayActivityInfo(
    val activityInfo: ActivityInfo,
    val label: String,
    val extendedInfo: CharSequence? = null,
    val icon: Drawable? = null,
    val resolvedInfo: ResolveInfo,
) : LogDumpable {
    companion object {
        val labelComparator = compareBy<DisplayActivityInfo> { it.compareLabel }
        private val valueAndLabelComparator = compareByDescending<DisplayActivityInfoStatus> { (_, status) ->
            status
        }.thenBy { (activityInfo, _) -> activityInfo.compareLabel }

        fun List<DisplayActivityInfoStatus>.sortByValueAndName() = sortedWith(valueAndLabelComparator)
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

    fun toPreferredApp(host: String, alwaysPreferred: Boolean): PreferredApp {
        return PreferredApp(
            host = host,
            packageName = packageName,
            component = flatComponentName,
            alwaysPreferred = alwaysPreferred
        )
    }

    override fun dump(stringBuilder: StringBuilder, hasher: LogHasher) = stringBuilder.commaSeparated {
        item {
            hasher.hash(this, "activityInfo=", activityInfo, HashProcessor.ActivityInfoProcessor)
        }
        item {
            hasher.hash(this, "label=", label, HashProcessor.StringProcessor)
        }
        itemNotNull(extendedInfo) {
            hasher.hash(this, "extendedInfo=", extendedInfo.toString(), HashProcessor.StringProcessor)
        }
        item {
            hasher.hash(this, "resolveInfo=", resolvedInfo, HashProcessor.ResolveInfoProcessor)
        }
    }

    override fun equals(other: Any?): Boolean {
        return (other as? DisplayActivityInfo)?.componentName == componentName
    }

    override fun hashCode(): Int = componentName.hashCode()
}
