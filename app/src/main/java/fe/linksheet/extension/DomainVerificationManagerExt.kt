package fe.linksheet.extension

import android.content.pm.verify.domain.DomainVerificationManager
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.S)
fun DomainVerificationManager.getAppHosts(packageName: String) =
    this.getDomainVerificationUserState(packageName)?.hostToStateMap?.keys ?: emptySet()