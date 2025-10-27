package fe.linksheet.util

import android.net.TrafficStats

fun <T> withStatsTag(trafficStatsTag: Int = 0xF00D, block: () -> T): T {
    try {
        TrafficStats.setThreadStatsTag(trafficStatsTag)
        return block()
    } finally {
        TrafficStats.clearThreadStatsTag()
    }
}
