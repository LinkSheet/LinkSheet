package fe.linksheet.module.unfurler

import me.saket.unfurl.Unfurler
import org.koin.dsl.module

val unfurlerModule = module {
    single<Unfurler> { Unfurler() }
}
