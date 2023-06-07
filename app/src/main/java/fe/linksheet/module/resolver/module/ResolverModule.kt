package fe.linksheet.module.resolver.module

import fe.linksheet.module.resolver.BrowserHandler
import fe.linksheet.module.resolver.BrowserResolver
import fe.linksheet.module.resolver.InAppBrowserHandler
import fe.linksheet.module.resolver.IntentResolver
import fe.linksheet.module.resolver.LibRedirectResolver
import fe.linksheet.module.resolver.urlresolver.amp2html.Amp2HtmlUrlResolver
import fe.linksheet.module.resolver.urlresolver.redirect.RedirectUrlResolver
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val resolverModule = module {
    singleOf(::IntentResolver)
    singleOf(::BrowserHandler)
    singleOf(::InAppBrowserHandler)
    singleOf(::RedirectUrlResolver)
    singleOf(::Amp2HtmlUrlResolver)
    singleOf(::LibRedirectResolver)
    singleOf(::BrowserResolver)
}