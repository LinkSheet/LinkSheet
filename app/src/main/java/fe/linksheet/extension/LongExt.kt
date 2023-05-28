package fe.linksheet.extension

import java.time.Instant
import java.time.LocalDateTime
import java.util.TimeZone

fun Long.unixMillisToLocalDateTime(): LocalDateTime = LocalDateTime.ofInstant(
    Instant.ofEpochMilli(this),
    TimeZone.getDefault().toZoneId()
)