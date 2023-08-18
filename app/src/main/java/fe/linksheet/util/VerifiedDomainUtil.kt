package fe.linksheet.util

import android.content.pm.ResolveInfo
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.os.Build
import androidx.annotation.RequiresApi

object VerifiedDomainUtil {
    @RequiresApi(Build.VERSION_CODES.S)
    fun ResolveInfo.hasVerifiedDomains(
        manager: DomainVerificationManager,
        linkHandlingAllowed: Boolean,
    ): Boolean {
        val packageName = activityInfo.packageName

        return manager.getDomainVerificationUserState(packageName)?.let { state ->
            val linkHandling =
                if (linkHandlingAllowed) state.isLinkHandlingAllowed else !state.isLinkHandlingAllowed
            val hasAnyVerifiedOrSelected = state.hostToStateMap.any {
                it.value == DomainVerificationUserState.DOMAIN_STATE_VERIFIED || it.value == DomainVerificationUserState.DOMAIN_STATE_SELECTED
            }

            linkHandling && hasAnyVerifiedOrSelected
        } ?: false
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun ResolveInfo.canHandleDomains(
        manager: DomainVerificationManager,
    ): Boolean {
        val packageName = activityInfo.packageName

        return manager.getDomainVerificationUserState(packageName)?.hostToStateMap?.isNotEmpty() == true
    }
}