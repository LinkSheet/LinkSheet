package fe.linksheet.web

import android.content.pm.ResolveInfo
import android.content.pm.verify.domain.DomainVerificationManager
import android.os.Build
import androidx.annotation.RequiresApi

@Deprecated(message = "No longer supported/needed")
object VerifiedDomainService {

    @RequiresApi(Build.VERSION_CODES.S)
    fun ResolveInfo.canHandleDomains(manager: DomainVerificationManager): Boolean {
        return manager.getDomainVerificationUserState(activityInfo.packageName)?.hostToStateMap?.isNotEmpty() == true
    }
}
