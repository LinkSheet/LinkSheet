@file:OptIn(ExperimentalTime::class)
@file:Suppress("FunctionName")

package fe.linksheet.module.clock

import fe.std.javatime.time.Timezone
import org.koin.core.module.Module
import org.koin.dsl.module
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toJavaInstant

fun ClockProviderModule(): Module {
    return module {
        single<ClockProvider> {
            ClockProvider(clock = Clock.System, zoneId = Timezone.SystemDefault)
        }
    }
}

class ClockProvider(
    private val clock: Clock,
    private val zoneId: ZoneId
) {
    fun now(): Instant {
        return clock.now()
    }

    fun nowZoned(): ZonedDateTime {
        return now().toJavaInstant().atZone(zoneId)
    }
}

