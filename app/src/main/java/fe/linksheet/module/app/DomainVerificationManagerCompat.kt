package fe.linksheet.module.app

import android.content.Context
import android.content.pm.verify.domain.DomainVerificationManager
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import fe.android.compose.version.AndroidVersion

interface DomainVerificationManagerCompat {
    fun getDomainVerificationUserState(packageName: String): VerificationState?
}

data class VerificationState(
    val hostToStateMap: Map<String, Int>,
    val isLinkHandlingAllowed: Boolean,
)

fun DomainVerificationManagerCompat(context: Context): DomainVerificationManagerCompat {
    return if (AndroidVersion.AT_LEAST_API_31_S) {
        val domainVerificationManager = context.getSystemService<DomainVerificationManager>()
        Api31Impl(domainVerificationManager)
    } else {
        PreApi31Impl
    }
}

object PreApi31Impl : DomainVerificationManagerCompat {
    override fun getDomainVerificationUserState(packageName: String): VerificationState? = null
}

@RequiresApi(31)
class Api31Impl(private val domainVerificationManager: DomainVerificationManager?) : DomainVerificationManagerCompat {

    override fun getDomainVerificationUserState(packageName: String): VerificationState? {
        return domainVerificationManager?.getDomainVerificationUserState(packageName)?.let { state ->
            VerificationState(state.hostToStateMap, state.isLinkHandlingAllowed)
        }
    }
}
