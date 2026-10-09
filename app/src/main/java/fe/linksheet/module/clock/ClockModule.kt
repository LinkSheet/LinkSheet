@file:OptIn(ExperimentalTime::class)
@file:Suppress("FunctionName")

package fe.linksheet.module.clock

import org.koin.dsl.module
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

val ClockModule = module {
    single<Clock> { Clock.System }
}
