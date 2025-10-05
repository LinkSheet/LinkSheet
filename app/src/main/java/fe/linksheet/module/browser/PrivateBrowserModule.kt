package fe.linksheet.module.browser

import app.linksheet.feature.browser.PrivateBrowsingService
import org.koin.dsl.module


val PrivateBrowsingModule = module {
    single {
        PrivateBrowsingService(context = get())
    }
}
