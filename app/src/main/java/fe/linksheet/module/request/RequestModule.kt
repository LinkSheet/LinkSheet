package fe.linksheet.module.request

import fe.httpkt.Request
import org.koin.dsl.module

val requestModule = module {
    single {
        Request()
    }
}