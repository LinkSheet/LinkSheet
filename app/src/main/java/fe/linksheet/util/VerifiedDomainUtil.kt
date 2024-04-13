package fe.linksheet.util

import android.content.pm.ResolveInfo
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.os.Build
import androidx.annotation.RequiresApi

object VerifiedDomainUtil {
    @RequiresApi(Build.VERSION_CODES.S)
    fun ResolveInfo.hasVerifiedDomains(manager: DomainVerificationManager, linkHandlingAllowed: Boolean): Boolean {
        val packageName = activityInfo.packageName
//        val state = manager.getDomainVerificationUserState(packageName) ?: return false
//        if (state.hostToStateMap.isEmpty()) return false
//
//        return if (linkHandlingAllowed) {
//            state.hostToStateMap.any { it.value == DomainVerificationUserState.DOMAIN_STATE_VERIFIED }
//        } else {
//            state.hostToStateMap.all { it.value != DomainVerificationUserState.DOMAIN_STATE_VERIFIED }
//        }


        return manager.getDomainVerificationUserState(packageName)?.let { state ->
            val linkHandling = if (linkHandlingAllowed) state.isLinkHandlingAllowed else !state.isLinkHandlingAllowed
            val hasAnyVerifiedOrSelected = state.hostToStateMap.any {
                it.value == DomainVerificationUserState.DOMAIN_STATE_VERIFIED || it.value == DomainVerificationUserState.DOMAIN_STATE_SELECTED
            }

            linkHandling && hasAnyVerifiedOrSelected
        } ?: false
    }


    @RequiresApi(Build.VERSION_CODES.S)
    fun ResolveInfo.canHandleDomains(manager: DomainVerificationManager): Boolean {
        return manager.getDomainVerificationUserState(activityInfo.packageName)?.hostToStateMap?.isNotEmpty() == true
    }
}
