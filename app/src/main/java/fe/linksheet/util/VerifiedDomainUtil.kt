package fe.linksheet.util

import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Stable
import fe.linksheet.experiment.ui.overhaul.composable.AppListItemData

object VerifiedDomainUtil {
    @RequiresApi(Build.VERSION_CODES.S)
    fun getStatus(
        manager: DomainVerificationManager,
        applicationInfo: ApplicationInfo,
        label: CharSequence,
    ): PackageDomainVerificationStatus? {
        val userState = manager.getDomainVerificationUserState(applicationInfo.packageName)
        if (userState == null || userState.hostToStateMap.isEmpty()) return null

        val stateNone = mutableListOf<String>()
        val stateSelected = mutableListOf<String>()
        val stateVerified = mutableListOf<String>()

        for ((host, state) in userState.hostToStateMap) {
            when (state) {
                DomainVerificationUserState.DOMAIN_STATE_NONE -> stateNone.add(host)
                DomainVerificationUserState.DOMAIN_STATE_SELECTED -> stateSelected.add(host)
                DomainVerificationUserState.DOMAIN_STATE_VERIFIED -> stateVerified.add(host)
            }
        }


//        val linkHandling =
//            if (linkHandlingAllowed) state.isLinkHandlingAllowed else !state.isLinkHandlingAllowed
//        val linkHandling = if (linkHandlingAllowed) state.isLinkHandlingAllowed else !state.isLinkHandlingAllowed
//        val hasAnyVerifiedOrSelected = state.hostToStateMap.any {
//            it.value == DomainVerificationUserState.DOMAIN_STATE_VERIFIED || it.value == DomainVerificationUserState.DOMAIN_STATE_SELECTED
//        }
//
//        linkHandling && hasAnyVerifiedOrSelected

        return PackageDomainVerificationStatus(
            applicationInfo,
            label,
            userState.isLinkHandlingAllowed,
            stateNone,
            stateSelected,
            stateVerified
        )
    }

    @Stable
    class PackageDomainVerificationStatus(
        applicationInfo: ApplicationInfo,
        label: CharSequence,
        val isLinkHandlingAllowed: Boolean,
        val stateNone: MutableList<String>,
        val stateSelected: MutableList<String>,
        val stateVerified: MutableList<String>,
    ) : AppListItemData(applicationInfo, label.toString()) {
        val enabled = isLinkHandlingAllowed && (stateVerified.isNotEmpty() || stateSelected.isNotEmpty())
        val hostSum = stateNone.size + stateSelected.size + stateVerified.size
//        val disabled = stateNone.isNotEmpty()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun ResolveInfo.canHandleDomains(manager: DomainVerificationManager): Boolean {
        return manager.getDomainVerificationUserState(activityInfo.packageName)?.hostToStateMap?.isNotEmpty() == true
    }
}
