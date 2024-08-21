package fe.linksheet.module.unfurler

import me.saket.unfurl.Unfurler
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val unfurlerModule = module {
    singleOf(::Unfurler)
}
