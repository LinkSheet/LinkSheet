package app.linksheet.feature.shizuku.core

import android.content.Context
import android.content.pm.verify.domain.DomainSet
import android.content.pm.verify.domain.DomainVerificationInfo
import android.content.pm.verify.domain.DomainVerificationState
import android.content.pm.verify.domain.IDomainVerificationManager
import android.os.Build
import android.os.UserHandleHidden
import android.util.Log
import androidx.annotation.RequiresApi
import app.linksheet.feature.shizuku.extension.wrapShizuku
import rikka.shizuku.SystemServiceHelper
import java.util.UUID


@RequiresApi(Build.VERSION_CODES.S)
object ShizukuDomainVerification {
//    private val iDomainVerificationManager by lazy {
//        IDomainVerificationManager.Stub.asInterface(
//            SystemServiceHelper.getSystemService(Context.DOMAIN_VERIFICATION_SERVICE).wrapShizuku()
//        )
//    }

    private fun getDomainVerificationManager(): IDomainVerificationManager {
        val binder = SystemServiceHelper.getSystemService(Context.DOMAIN_VERIFICATION_SERVICE)
        return IDomainVerificationManager.Stub.asInterface(binder.wrapShizuku())
    }

    fun queryValidVerificationPackageNames(): List<String> {
        val packages = getDomainVerificationManager().queryValidVerificationPackageNames()
        return packages
    }

    fun getAllDomainVerificationInfos(): List<DomainVerificationInfo> {
        Log.d("ShizukuDomainVerification", "queryValidVerificationPackageNames")
        val packageNames = getDomainVerificationManager().queryValidVerificationPackageNames()
        return packageNames.mapNotNull {
            Log.d("ShizukuDomainVerification", "getDomainVerificationInfo($it)")
            getDomainVerificationManager().getDomainVerificationInfo(it)
        }
    }

    fun setDomainVerificationLinkHandlingAllowed(packageName: String, allowed: Boolean) {
        getDomainVerificationManager().setDomainVerificationLinkHandlingAllowed(
            packageName,
            allowed,
            UserHandleHidden.myUserId()
        )
    }

    fun setDomainVerification(domainSetId: UUID, domains: Set<String>, state: DomainVerificationState2): Int {
        return getDomainVerificationManager().setDomainVerificationStatus(domainSetId.toString(),
            DomainSet(domains), state.value)
    }
}

enum class DomainVerificationState2(val value: Int) {
    NoResponse(DomainVerificationState.STATE_NO_RESPONSE),
    Success(DomainVerificationState.STATE_SUCCESS),
    Approved(DomainVerificationState.STATE_APPROVED),
    Denied(DomainVerificationState.STATE_DENIED),
    Migrated(DomainVerificationState.STATE_MIGRATED),
    Restored(DomainVerificationState.STATE_RESTORED),
    LegacyFailure(DomainVerificationState.STATE_LEGACY_FAILURE),
    SysConfig(DomainVerificationState.STATE_SYS_CONFIG),
    PreVerified(DomainVerificationState.STATE_PRE_VERIFIED),
    FirstVerifierDefined(DomainVerificationState.STATE_FIRST_VERIFIER_DEFINED)
}
