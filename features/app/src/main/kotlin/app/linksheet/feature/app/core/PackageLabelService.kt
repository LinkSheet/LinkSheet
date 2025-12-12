package app.linksheet.feature.app.core

import android.content.pm.ApplicationInfo
import android.content.pm.ComponentInfo
import android.content.pm.ResolveInfo
import fe.composekit.extension.info

interface PackageLabelService {
    fun loadComponentInfoLabel(componentInfo: ComponentInfo): String?
    fun findBestLabel(applicationInfo: ApplicationInfo, launcher: ResolveInfo?): String
    fun findBestLabel(componentInfo: ComponentInfo): String
    fun findApplicationLabel(applicationInfo: ApplicationInfo): String
}

class DefaultPackageLabelService(
    private val loadComponentInfoLabelInternal: (ComponentInfo) -> CharSequence,
    private val getApplicationLabel: (ApplicationInfo) -> CharSequence,
) : PackageLabelService {

    override fun loadComponentInfoLabel(componentInfo: ComponentInfo): String? {
        val label = loadComponentInfoLabelInternal(componentInfo)
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

    override fun findBestLabel(componentInfo: ComponentInfo): String {
        return loadComponentInfoLabel(componentInfo) ?: findApplicationLabel(componentInfo.applicationInfo)
    }

    override fun findApplicationLabel(applicationInfo: ApplicationInfo): String {
        val appLabel = getApplicationLabel(applicationInfo)
        if (appLabel.isNotEmpty()) return appLabel.toString()

        return applicationInfo.packageName
    }
}
