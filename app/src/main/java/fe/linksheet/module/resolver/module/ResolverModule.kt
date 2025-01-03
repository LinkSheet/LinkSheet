package fe.linksheet.module.resolver.module

import android.content.Context
import fe.linksheet.module.resolver.ImprovedBrowserHandler
import fe.linksheet.module.resolver.ImprovedIntentResolver
import fe.linksheet.module.resolver.*
import fe.linksheet.module.resolver.browser.BrowserHandler
import fe.linksheet.module.resolver.urlresolver.amp2html.Amp2HtmlUrlResolver
import fe.linksheet.module.resolver.urlresolver.redirect.RedirectUrlResolver
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val resolverModule = module {
    single { BrowserResolver(get<Context>().packageManager, get()) }
    singleOf(::BrowserHandler)
    singleOf(::ImprovedBrowserHandler)
    singleOf(::InAppBrowserHandler)
    singleOf(::RedirectUrlResolver)
    singleOf(::Amp2HtmlUrlResolver)
    singleOf(::LibRedirectResolver)
    singleOf(::ImprovedIntentResolver)
}
