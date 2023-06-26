package fe.linksheet.module.resolver.urlresolver.base

import fe.httpkt.Request
import fe.linksheet.extension.createLogger
import fe.linksheet.module.log.Logger
import fe.linksheet.supabaseApiKey
import fe.linksheet.supabaseFunctionHost
import org.koin.dsl.module

val allRemoteResolveRequest = module {
    single {
        AllRemoteResolveRequest(
            "$supabaseFunctionHost/all",
            supabaseApiKey,
            get(), createLogger<AllRemoteResolveRequest>()
        )
    }
}

class AllRemoteResolveRequest(
    apiUrl: String,
    token: String,
    request: Request,
    logger: Logger
) : RemoteResolveRequest(apiUrl, token, request, logger, "redirect", "amp2html") {

}