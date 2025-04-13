package fe.linksheet.module.resolver.urlresolver.base

import fe.droidkit.koin.single
import fe.httpkt.Request
import fe.linksheet.util.buildconfig.LinkSheetAppConfig
import org.koin.dsl.module


val allRemoteResolveRequest = module {
    single<AllRemoteResolveRequest, Request> { _, request ->
        AllRemoteResolveRequest(
            "${LinkSheetAppConfig.supabaseHost()}/all",
            LinkSheetAppConfig.supabaseApiKey(),
            request
        )
    }
}

class AllRemoteResolveRequest(
    apiUrl: String,
    token: String,
    request: Request,
) : RemoteResolveRequest(apiUrl, token, request, "redirect", "amp2html")
