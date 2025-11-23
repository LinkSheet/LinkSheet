package fe.linksheet.module.resolver.urlresolver.base

import fe.linksheet.module.resolver.urlresolver.ResolveResultType

class ResolveRequestException(statusCode: Int? = null) : Exception("$statusCode")

interface LocalResolveRequest {
    suspend fun resolveLocal(url: String, timeout: Int): Result<ResolveResultType>
}
