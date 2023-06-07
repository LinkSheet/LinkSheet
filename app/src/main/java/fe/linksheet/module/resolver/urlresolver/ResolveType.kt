package fe.linksheet.module.resolver.urlresolver

import androidx.annotation.StringRes
import fe.linksheet.R

sealed class ResolveType(@StringRes val stringId: Int, val url: String) {
    class Cache(url: String) : ResolveType(R.string.redirect_resolve_type_cache, url)
    class Remote(url: String) : ResolveType(R.string.redirect_resolve_type_remote, url)
    class Local(url: String) : ResolveType(R.string.redirect_resolve_type_local, url)
    class NotResolved(url: String) : ResolveType(R.string.redirect_resolve_type_not_resolved, url)
}