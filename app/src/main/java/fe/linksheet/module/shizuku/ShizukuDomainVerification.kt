package fe.linksheet.module.shizuku

import android.content.Context
import android.content.pm.verify.domain.IDomainVerificationManager
import android.os.Build
import android.os.UserHandleHidden
import androidx.annotation.RequiresApi
import rikka.shizuku.SystemServiceHelper

@RequiresApi(Build.VERSION_CODES.S)
class ShizukuDomainVerification {
    private val iDomainVerificationManager by lazy {
        IDomainVerificationManager.Stub.asInterface(SystemServiceHelper.getSystemService(Context.DOMAIN_VERIFICATION_SERVICE).wrapShizuku())
    }

    fun setDomainVerificationLinkHandlingAllowed(packageName: String, allowed: Boolean) {
        iDomainVerificationManager.setDomainVerificationLinkHandlingAllowed(
            packageName,
            allowed,
            UserHandleHidden.myUserId()
        )
    }
}

interface ShizukuDomainVerificationManager {
    fun setDomainVerificationLinkHandlingAllowed(packageName: String, allowed: Boolean)
}


@RequiresApi(Build.VERSION_CODES.S)
private class Impl(private val manager: IDomainVerificationManager) : ShizukuDomainVerificationManager {
    override fun setDomainVerificationLinkHandlingAllowed(packageName: String, allowed: Boolean) {
        manager.setDomainVerificationLinkHandlingAllowed(
            packageName,
            allowed,
            UserHandleHidden.myUserId()
        )
    }
}
