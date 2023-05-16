package fe.linksheet.extension

import android.content.pm.ResolveInfo
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.S)
fun DomainVerificationManager.getAppHosts(packageName: String) =
    getDomainVerificationUserState(packageName)?.hostToStateMap?.keys ?: emptySet()


@RequiresApi(Build.VERSION_CODES.S)
fun DomainVerificationManager.hasVerifiedDomains(
    resolveInfo: ResolveInfo,
    linkHandlingAllowed: Boolean
) = getDomainVerificationUserState(resolveInfo.activityInfo.packageName)?.let { state ->
    val linkHandling = if (linkHandlingAllowed) state.isLinkHandlingAllowed else !state.isLinkHandlingAllowed
    val hasAnyVerifiedOrSelected = state.hostToStateMap.any {
        it.value == DomainVerificationUserState.DOMAIN_STATE_VERIFIED || it.value == DomainVerificationUserState.DOMAIN_STATE_SELECTED
    }

    linkHandling && hasAnyVerifiedOrSelected
} ?: false
