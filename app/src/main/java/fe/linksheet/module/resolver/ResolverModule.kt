package fe.linksheet.module.resolver

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val resolverModule = module {
    singleOf(::IntentResolver)
    singleOf(::BrowserHandler)
    singleOf(::InAppBrowserHandler)
    singleOf(::RedirectFollower)
    singleOf(::LibRedirectResolver)
    singleOf(::BrowserResolver)
}