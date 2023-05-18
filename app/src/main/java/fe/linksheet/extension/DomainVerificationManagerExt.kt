package fe.linksheet.extension

import android.content.Context
import android.content.pm.ResolveInfo
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.S)
fun DomainVerificationManager.getAppHosts(packageName: String) = getDomainVerificationUserState(
    packageName
)?.hostToStateMap?.keys ?: emptySet()

@RequiresApi(Build.VERSION_CODES.S)
fun DomainVerificationManager.getDisplayActivityInfos(
    context: Context,
    linkHandlingAllowed: Boolean,
    filter: ((ResolveInfo) -> Boolean)? = null
) = context.packageManager.queryAllResolveInfos(true)
    .filterIfFilterIsNotNull(filter)
    .filter { it.hasVerifiedDomains(this, linkHandlingAllowed) }
    .toDisplayActivityInfo(context, true)

@RequiresApi(Build.VERSION_CODES.S)
fun DomainVerificationManager.getDisplayActivityInfos(
    context: Context
) = context.packageManager.queryAllResolveInfos(true).toDisplayActivityInfo(context, true)