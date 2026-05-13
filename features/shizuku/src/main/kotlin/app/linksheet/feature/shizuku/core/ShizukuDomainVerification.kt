package app.linksheet.feature.shizuku

import android.content.Context
import android.content.pm.verify.domain.IDomainVerificationManager
import android.os.Build
import android.os.UserHandleHidden
import androidx.annotation.RequiresApi
import app.linksheet.feature.shizuku.extension.wrapShizuku
import rikka.shizuku.SystemServiceHelper


@RequiresApi(Build.VERSION_CODES.S)
object ShizukuDomainVerification {
    private val iDomainVerificationManager by lazy {
        IDomainVerificationManager.Stub.asInterface(
            SystemServiceHelper.getSystemService(Context.DOMAIN_VERIFICATION_SERVICE).wrapShizuku()
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
