package fe.linksheet.module.language

import org.koin.dsl.module

val AppLocaleModule = module {
    single { AndroidAppLocaleService() }
}
