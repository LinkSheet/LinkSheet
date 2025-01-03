package fe.linksheet.extension.compose

import android.content.Context
import android.content.pm.ResolveInfo
import android.content.pm.verify.domain.DomainVerificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import fe.kotlin.extension.iterable.filterIfPredicateIsNotNull
import fe.kotlin.`typealias`.KtPredicate
import fe.linksheet.extension.android.queryAllResolveInfos
import fe.linksheet.extension.android.toDisplayActivityInfos
import fe.linksheet.module.app.ActivityAppInfo

@RequiresApi(Build.VERSION_CODES.S)
fun DomainVerificationManager.getAppHosts(packageName: String) = getDomainVerificationUserState(
    packageName
)?.hostToStateMap?.keys ?: emptySet()

@RequiresApi(Build.VERSION_CODES.S)
fun DomainVerificationManager.getDisplayActivityInfos(
    context: Context,
    filter: KtPredicate<ResolveInfo>? = null
): List<ActivityAppInfo> {
    return context.packageManager.queryAllResolveInfos(true)
        .filterIfPredicateIsNotNull(filter)
        .toDisplayActivityInfos(context, true)
}
