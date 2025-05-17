package fe.linksheet.module.resolver

import android.net.Uri
import androidx.annotation.StringRes
import fe.linksheet.R
import fe.linksheet.module.resolver.urlresolver.ResolveResultType
import fe.linksheet.util.StringResHolder
import java.net.UnknownHostException
import androidx.core.net.toUri

class ResolveModuleStatus {
    private val _resolved = mutableMapOf<ResolveModule, Result<ResolveResultType>?>()
    val resolved: Map<ResolveModule, Result<ResolveResultType>?> = _resolved

    var globalFailure: GlobalFailure? = null
        private set

    sealed class GlobalFailure(@param:StringRes override val id: Int, vararg arg: Any) : StringResHolder {
        data object NoInternet : GlobalFailure(R.string.no_internet_connection)
        class UnknownHost(val host: String) : GlobalFailure(R.string.unknown_host, host)

        override val args = arrayOf(*arg)
    }

    suspend fun resolveIfEnabled(
        enabled: Boolean,
        resolveModule: ResolveModule,
        uri: Uri?,
        resolve: suspend (Uri) -> Result<ResolveResultType>?,
    ): Uri? {
        if (!enabled || uri == null || globalFailure != null) return uri

        val resolveResult = resolve(uri)
        _resolved[resolveModule] = resolveResult

        val exception = resolveResult?.exceptionOrNull()
        if (exception is UnknownHostException) {
            globalFailure = GlobalFailure.UnknownHost(uri.host!!)
            return uri
        }

        val resultType = resolveResult?.getOrNull()
        if (resultType is ResolveResultType.NoInternetConnection) {
            globalFailure = GlobalFailure.NoInternet
            return uri
        }

        if (resultType is ResolveResultType.Resolved) {
            return resultType.url.toUri()
        }

        return uri
    }
}
