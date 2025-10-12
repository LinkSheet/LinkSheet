package fe.linksheet.module.shizuku

import android.content.Context
import android.content.pm.verify.domain.IDomainVerificationManager
import android.os.Build
import android.os.UserHandleHidden
import androidx.annotation.RequiresApi
import fe.std.result.IResult
import fe.std.result.getOrNull
import fe.std.result.isFailure
import fe.std.result.unaryPlus
import rikka.shizuku.SystemServiceHelper

@RequiresApi(Build.VERSION_CODES.S)
class ShizukuDomainVerification {
    private val iDomainVerificationManager by lazy {
        IDomainVerificationManager.Stub.asInterface(
            SystemServiceHelper.getSystemService(Context.DOMAIN_VERIFICATION_SERVICE).wrapShizuku().getOrNull()!!
        )
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

@RequiresApi(Build.VERSION_CODES.S)
private fun getShizukuDomainVerificationManager(): Impl? {
    val systemService = SystemServiceHelper.getSystemService(Context.DOMAIN_VERIFICATION_SERVICE)
    val wrapped = systemService.wrapShizuku()
    if (wrapped.isFailure()) {
        return null
    }

    val manager = IDomainVerificationManager.Stub.asInterface(wrapped.value)
    return Impl(manager)
}
