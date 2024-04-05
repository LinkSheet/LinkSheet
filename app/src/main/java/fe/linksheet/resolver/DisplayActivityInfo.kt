package fe.linksheet.resolver

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import android.os.Parcel
import androidx.compose.ui.graphics.ImageBitmap
import fe.linksheet.extension.android.componentName
import fe.linksheet.extension.android.getIcon
import fe.linksheet.extension.android.toImageBitmap
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.redactor.Redactable
import fe.linksheet.module.redactor.Redactor
import fe.stringbuilder.util.commaSeparated

typealias DisplayActivityInfoStatus = Pair<DisplayActivityInfo, Boolean>

data class DisplayActivityInfo(
    val resolvedInfo: ResolveInfo,
    val label: String,
    val browser: Boolean = false,
    val fallback: Boolean = false,
) : Redactable<DisplayActivityInfo> {
    companion object {
        val labelComparator = compareBy<DisplayActivityInfo> { it.compareLabel }
        private val valueAndLabelComparator = compareByDescending<DisplayActivityInfoStatus> { (_, status) ->
            status
        }.thenBy { (activityInfo, _) -> activityInfo.compareLabel }

        fun List<DisplayActivityInfoStatus>.sortByValueAndName() = sortedWith(valueAndLabelComparator)

    }

    private val activityInfo = resolvedInfo.activityInfo

    val compareLabel = label.lowercase()
    val packageName: String = activityInfo.packageName
    val componentName by lazy { activityInfo.componentName() }
    val flatComponentName by lazy { componentName.flattenToString() }

    private var icon: ImageBitmap? = null

    fun getIcon(context: Context): ImageBitmap {
        if (icon == null) {
            icon = activityInfo.getIcon(context)!!.toImageBitmap()
        }

        return icon!!
    }

    fun toPreferredApp(host: String, alwaysPreferred: Boolean): PreferredApp {
        return PreferredApp.new(
            host = host,
            pkg = packageName,
            cmp = componentName,
            always = alwaysPreferred
        )
    }

    override fun process(builder: StringBuilder, redactor: Redactor): StringBuilder {
        return builder.commaSeparated {
//            item {
//                redactor.process(this, activityInfo, HashProcessor.ActivityInfoProcessor, "activityInfo=")
//            }
//            item {
//                redactor.process(this, label, HashProcessor.StringProcessor, "label=")
//            }
//            itemNotNull(extendedInfo) {
//                redactor.process(this, extendedInfo.toString(), HashProcessor.StringProcessor, "extendedInfo=")
//            }
//            item {
//                redactor.process(this, resolvedInfo, HashProcessor.ResolveInfoProcessor, "resolveInfo=")
//            }
        }
    }

    override fun equals(other: Any?): Boolean {
        return (other as? DisplayActivityInfo)?.componentName == componentName
    }

    override fun hashCode(): Int = componentName.hashCode()
}
