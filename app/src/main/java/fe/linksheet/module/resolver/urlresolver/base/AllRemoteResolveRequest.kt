package fe.linksheet.module.resolver.urlresolver.base

import fe.httpkt.Request


class AllRemoteResolveRequest(
    apiUrl: String,
    token: String,
    request: Request,
) : RemoteResolveRequest(apiUrl, token, request, "redirect", "amp2html")
