package fe.linksheet.module.resolver.module

import android.content.Context
import fe.linksheet.module.resolver.BrowserHandler
import fe.linksheet.module.resolver.BrowserResolver
import fe.linksheet.module.resolver.InAppBrowserHandler
import fe.linksheet.module.resolver.IntentResolver
import fe.linksheet.module.resolver.LibRedirectResolver
import fe.linksheet.module.resolver.urlresolver.amp2html.Amp2HtmlUrlResolver
import fe.linksheet.module.resolver.urlresolver.redirect.RedirectUrlResolver
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import fe.linksheet.experiment.improved.resolver.ImprovedIntentResolver

val resolverModule = module {
    single { BrowserResolver(get<Context>().packageManager) }
    singleOf(::BrowserHandler)

    singleOf(::IntentResolver)
    singleOf(::InAppBrowserHandler)
    singleOf(::RedirectUrlResolver)
    singleOf(::Amp2HtmlUrlResolver)
    singleOf(::LibRedirectResolver)
    singleOf(::ImprovedIntentResolver)
}
