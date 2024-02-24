package fe.linksheet.module.resolver.urlresolver.base

import fe.httpkt.Request
import fe.linksheet.LinkSheetAppConfig
import fe.linksheet.extension.koin.createLogger
import fe.linksheet.module.log.impl.Logger
import org.koin.dsl.module

val allRemoteResolveRequest = module {
    single {
        AllRemoteResolveRequest(
            "${LinkSheetAppConfig.supabaseHost()}/all",
            LinkSheetAppConfig.supabaseApiKey(),
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
