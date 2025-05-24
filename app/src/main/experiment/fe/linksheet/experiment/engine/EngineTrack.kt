package fe.linksheet.experiment.engine

import fe.linksheet.experiment.engine.context.EngineRunContext
import fe.linksheet.module.resolver.util.AndroidAppPackage
import fe.std.uri.StdUrl
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class EngineTrack(
    val id: Uuid,
    val position: Int,
    private val predicate: EngineTrackPredicate,
    private val engine: LinkEngine
) {
    fun matches(input: EngineTrackInput): Boolean {
        return predicate.evaluate(input)
    }

    suspend fun run(url: StdUrl, context: EngineRunContext): ContextualEngineResult {
        return engine.process(url, context)
    }
}

fun interface EngineTrackPredicate {
    fun evaluate(input: EngineTrackInput): Boolean
}

data class EngineTrackInput(val url: StdUrl, val referrer: AndroidAppPackage?)

class TrackSelector(tracks: List<EngineTrack>) {
    private val tracks = tracks.sortedBy { it.position }

    fun findTrack(input: EngineTrackInput): EngineTrack? {
        val track = tracks.firstOrNull { it.matches(input) }
        return track
    }
}
