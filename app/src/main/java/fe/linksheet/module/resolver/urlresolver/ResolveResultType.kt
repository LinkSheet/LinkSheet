package fe.linksheet.module.resolver.urlresolver

import androidx.annotation.StringRes
import fe.linksheet.R
import fe.linksheet.util.StringResHolder

sealed interface ResolveResultType {
    sealed class Resolved(@StringRes override val id: Int, val url: String) : ResolveResultType, StringResHolder {
        class LocalCache(url: String) : Resolved(R.string.redirect_resolve_type_local_cache, url)
        class BuiltInCache(url: String) : Resolved(R.string.redirect_resolve_type_builtin_cache, url)
        class Remote(url: String) : Resolved(R.string.redirect_resolve_type_remote, url)
        class Local(url: String) : Resolved(R.string.redirect_resolve_type_local, url)
    }

    data object NoInternetConnection : ResolveResultType

    fun success(): Result<ResolveResultType> {
        return Result.success(this)
    }
}
