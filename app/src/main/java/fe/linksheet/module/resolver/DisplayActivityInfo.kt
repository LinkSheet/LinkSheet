package fe.linksheet.module.resolver

import android.content.pm.ResolveInfo
import androidx.compose.ui.graphics.ImageBitmap
import fe.linksheet.extension.android.componentName
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.redactor.ProtectedStringBuilder
import fe.linksheet.module.redactor.Redactable
import fe.stringbuilder.util.Bracket.Round
import fe.stringbuilder.util.Separator
import fe.stringbuilder.util.separated
import fe.stringbuilder.util.wrapped

typealias DisplayActivityInfoStatus = Pair<DisplayActivityInfo, Boolean>

data class DisplayActivityInfo(
    val resolvedInfo: ResolveInfo,
    val label: String,
    val browser: Boolean = false,
    var icon: Lazy<ImageBitmap>,
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

//    fun getIcon(context: Context): ImageBitmap {
//        if (icon == null) {
//            icon = activityInfo.getIcon(context)!!.toImageBitmap()
//        }
//
//        return icon!!
//    }

    fun toPreferredApp(host: String, alwaysPreferred: Boolean): PreferredApp {
        return PreferredApp.Companion.new(
            host = host,
            pkg = packageName,
            cmp = componentName,
            always = alwaysPreferred
        )
    }

    override fun buildString(builder: ProtectedStringBuilder) {
        builder.wrapped(Round) {
            separated(Separator.Comma) {
//                item { sensitive("ampUrl", StringUrl(ampUrl)) }
//                item { sensitive("canonicalUrl", StringUrl(canonicalUrl)) }
//                item(prefix = "activityInfo=") {
//                    sensitive("activityInfo", )
//                    redactor.process(this, activityInfo, HashProcessor.ActivityInfoProcessor)
//                }
//                item(prefix = "label=") {
//                    redactor.process(this, label, HashProcessor.StringProcessor)
//                }
//                //            itemNotNull(extendedInfo) {
//                //                redactor.process(this, extendedInfo.toString(), HashProcessor.StringProcessor, "extendedInfo=")
//                //            }
//                item(prefix = "resolveInfo=") {
//                    redactor.process(this, resolvedInfo, HashProcessor.ResolveInfoProcessor)
//                }
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        return (other as? DisplayActivityInfo)?.componentName == componentName
    }

    override fun hashCode(): Int = componentName.hashCode()
}
