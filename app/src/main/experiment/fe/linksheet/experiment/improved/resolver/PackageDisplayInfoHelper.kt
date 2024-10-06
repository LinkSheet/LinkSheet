package fe.linksheet.experiment.improved.resolver

import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo
import androidx.annotation.VisibleForTesting
import fe.linksheet.resolver.DisplayActivityInfo

class PackageDisplayInfoHelper(
    private val loadLabel: (ResolveInfo) -> CharSequence,
    private val getApplicationLabel: (ApplicationInfo) -> CharSequence,
) {
    fun createDisplayActivityInfo(resolveInfo: ResolveInfo, isBrowser: Boolean): DisplayActivityInfo {
        return DisplayActivityInfo(
            resolvedInfo = resolveInfo,
            label = findBestLabel(resolveInfo),
            browser = isBrowser
        )
    }

    @VisibleForTesting
    fun findBestLabel(resolveInfo: ResolveInfo): String {
        val label = loadLabel(resolveInfo)
        if (label.isNotEmpty()) return label.toString()

        val appLabel = getApplicationLabel(resolveInfo.activityInfo.applicationInfo)
        if(appLabel.isNotEmpty()) return appLabel.toString()

        return resolveInfo.resolvePackageName
    }
}
