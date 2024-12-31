package fe.linksheet.module.app

import androidx.compose.runtime.Stable
import fe.linksheet.composable.AppListItemData

@Stable
class PackageDomainVerificationStatus(
    packageName: String,
    label: CharSequence,
    val flags: Int,
    val isLinkHandlingAllowed: Boolean,
    val stateNone: MutableList<String>,
    val stateSelected: MutableList<String>,
    val stateVerified: MutableList<String>,
) : AppListItemData(packageName, label.toString()) {

    val enabled = isLinkHandlingAllowed && (stateVerified.isNotEmpty() || stateSelected.isNotEmpty())
    val hostSum = stateNone.size + stateSelected.size + stateVerified.size
//        val disabled = stateNone.isNotEmpty()
}
