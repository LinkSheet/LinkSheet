package fe.linksheet.feature.app.`package`

import android.content.pm.ApplicationInfo
import android.content.pm.ComponentInfo
import android.content.pm.ResolveInfo
import fe.linksheet.util.extension.android.info

interface PackageLabelService {
    fun loadComponentInfoLabel(info: ComponentInfo): String?
    fun findBestLabel(applicationInfo: ApplicationInfo, launcher: ResolveInfo?): String
    fun findApplicationLabel(applicationInfo: ApplicationInfo): String
}

class DefaultPackageLabelService(
    private val loadComponentInfoLabelInternal: (ComponentInfo) -> CharSequence,
    private val getApplicationLabel: (ApplicationInfo) -> CharSequence,
) : PackageLabelService {

    override fun loadComponentInfoLabel(info: ComponentInfo): String? {
        val label = loadComponentInfoLabelInternal(info)
        if (label.isNotEmpty()) return label.toString()

        return null
    }


    override fun findBestLabel(applicationInfo: ApplicationInfo, launcher: ResolveInfo?): String {
        if (launcher != null) {
            val label = loadComponentInfoLabel(launcher.info)
            if (label != null) return label
        }

        return applicationInfo.let(::findApplicationLabel)
    }

    override fun findApplicationLabel(applicationInfo: ApplicationInfo): String {
        val appLabel = getApplicationLabel(applicationInfo)
        if (appLabel.isNotEmpty()) return appLabel.toString()

        return applicationInfo.packageName
    }
}
