package fe.linksheet.module.resolver.urlresolver.base

import fe.httpkt.Request
import fe.linksheet.LinkSheetAppConfig
import fe.linksheet.extension.koin.single
import fe.linksheet.module.log.Logger
import org.koin.dsl.module


val allRemoteResolveRequest = module {
    single<AllRemoteResolveRequest, Request> { _, request ->
        AllRemoteResolveRequest(
            "${LinkSheetAppConfig.supabaseHost()}/all",
            LinkSheetAppConfig.supabaseApiKey(),
            request, serviceLogger
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
