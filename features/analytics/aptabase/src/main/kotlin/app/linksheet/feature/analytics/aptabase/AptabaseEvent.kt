package app.linksheet.feature.analytics.aptabase

internal data class AptabaseEvent(
    val timestamp: String,
    val sessionId: String,
    val eventName: String,
    val systemProps: EnvironmentInfo,
    val props: Map<String, Any>,
)
