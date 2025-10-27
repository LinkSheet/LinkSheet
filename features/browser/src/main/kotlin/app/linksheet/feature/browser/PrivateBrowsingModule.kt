package app.linksheet.feature.browser

import org.koin.dsl.module

val PrivateBrowsingModule = module {
    single {
        PrivateBrowsingService(context = get())
    }
}
