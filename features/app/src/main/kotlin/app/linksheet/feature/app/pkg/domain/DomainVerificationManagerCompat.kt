package app.linksheet.feature.app.pkg.domain

import android.content.Context
import android.content.pm.verify.domain.DomainVerificationManager
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import fe.composekit.core.AndroidVersion

fun interface DomainVerificationManagerCompat {
    fun getDomainVerificationUserState(packageName: String): VerificationStateCompat?
}

fun DomainVerificationManagerCompat(context: Context): DomainVerificationManagerCompat {
    return when {
        AndroidVersion.isAtLeastApi31S() -> Api31Impl(
            domainVerificationManager = context.getSystemService<DomainVerificationManager>()
        )
        else -> PreApi31Impl
    }
}

private object PreApi31Impl : DomainVerificationManagerCompat {
    override fun getDomainVerificationUserState(packageName: String): VerificationStateCompat {
        return VerificationUnsupportedState
    }
}

@RequiresApi(31)
private class Api31Impl(private val domainVerificationManager: DomainVerificationManager?) : DomainVerificationManagerCompat {

    override fun getDomainVerificationUserState(packageName: String): VerificationStateCompat? {
        return domainVerificationManager
            ?.getDomainVerificationUserState(packageName)
            ?.takeIf { it.hostToStateMap.isNotEmpty() }
            ?.let {
                VerificationState(it.hostToStateMap, it.isLinkHandlingAllowed)
            }
    }
}
