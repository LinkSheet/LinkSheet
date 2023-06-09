package fe.linksheet.extension.compose

import android.content.Context
import android.content.pm.ResolveInfo
import android.content.pm.verify.domain.DomainVerificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import fe.linksheet.extension.filterIfFilterIsNotNull
import fe.linksheet.extension.android.queryAllResolveInfos
import fe.linksheet.extension.android.toDisplayActivityInfos

@RequiresApi(Build.VERSION_CODES.S)
fun DomainVerificationManager.getAppHosts(packageName: String) = getDomainVerificationUserState(
    packageName
)?.hostToStateMap?.keys ?: emptySet()

@RequiresApi(Build.VERSION_CODES.S)
fun DomainVerificationManager.getDisplayActivityInfos(
    context: Context,
    filter: ((ResolveInfo) -> Boolean)? = null
) = context.packageManager.queryAllResolveInfos(true).filterIfFilterIsNotNull(filter).toDisplayActivityInfos(context, true)