package app.linksheet.feature.shizuku

import android.content.Context
import android.content.Intent
import android.content.pm.verify.domain.IDomainVerificationManager
import android.os.Build
import android.os.UserHandleHidden
import androidx.annotation.RequiresApi
import app.linksheet.api.eventbus.IntentEventHandler
import app.linksheet.feature.shizuku.extension.wrapShizuku
import fe.linksheet.util.IntentFilters
import rikka.shizuku.SystemServiceHelper


@RequiresApi(Build.VERSION_CODES.S)
class ShizukuDomainVerification : IntentEventHandler {
    private val iDomainVerificationManager by lazy {
        IDomainVerificationManager.Stub.asInterface(
            SystemServiceHelper.getSystemService(Context.DOMAIN_VERIFICATION_SERVICE).wrapShizuku()
        )
    }
    override val filter = IntentFilters.packageState

    override fun onReceive(context: Context, intent: Intent) {

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


//@RequiresApi(Build.VERSION_CODES.S)
//private class Impl(private val manager: IDomainVerificationManager) : ShizukuDomainVerificationManager {
//    override fun setDomainVerificationLinkHandlingAllowed(packageName: String, allowed: Boolean) {
//        manager.setDomainVerificationLinkHandlingAllowed(
//            packageName,
//            allowed,
//            UserHandleHidden.myUserId()
//        )
//    }
//}
//val verification = ShizukuDomainVerification()
//verification.setDomainVerificationLinkHandlingAllowed("com.looker.droidify", false)
