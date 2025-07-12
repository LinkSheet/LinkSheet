@file:OptIn(ExperimentalTime::class)
@file:Suppress("FunctionName")

package fe.linksheet.module.clock

import fe.std.javatime.time.Timezone
import org.koin.dsl.module
import java.time.ZoneId
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

val ClockModule = module {
    single<Clock> { Clock.System }
    single<ZoneId> { Timezone.SystemDefault }
}
