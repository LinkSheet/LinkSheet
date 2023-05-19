package fe.linksheet.extension

import android.content.Context
import android.content.pm.ResolveInfo
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.os.Build
import androidx.annotation.RequiresApi
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import fe.linksheet.util.applyIf

fun ResolveInfo.toDisplayActivityInfo(context: Context) = DisplayActivityInfo(
    activityInfo = activityInfo,
    label = loadLabel(context.packageManager).toString(),
    icon = activityInfo.getIcon(context),
    resolvedInfo = this
)

fun Iterable<ResolveInfo>.toDisplayActivityInfos(
    context: Context,
    sorted: Boolean = true
) = map { it.toDisplayActivityInfo(context) }.applyIf(sorted) {
    sortedWith(DisplayActivityInfo.labelComparator)
}

fun Iterable<ResolveInfo>.toPackageKeyedMap() = associateBy { it.activityInfo.packageName }

@RequiresApi(Build.VERSION_CODES.S)
fun ResolveInfo.hasVerifiedDomains(
    manager: DomainVerificationManager,
    linkHandlingAllowed: Boolean
) = manager.getDomainVerificationUserState(activityInfo.packageName)?.let { state ->
    val linkHandling = if (linkHandlingAllowed) state.isLinkHandlingAllowed else !state.isLinkHandlingAllowed
    val hasAnyVerifiedOrSelected = state.hostToStateMap.any {
        it.value == DomainVerificationUserState.DOMAIN_STATE_VERIFIED || it.value == DomainVerificationUserState.DOMAIN_STATE_SELECTED
    }

    linkHandling && hasAnyVerifiedOrSelected
} ?: false