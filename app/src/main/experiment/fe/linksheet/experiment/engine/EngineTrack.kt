package fe.linksheet.experiment.engine

import fe.linksheet.module.resolver.util.AndroidAppPackage
import fe.std.uri.StdUrl
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class EngineTrack(
    val id: Uuid,
    val position: Int,
    val predicate: EngineTrackPredicate,
    val engine: LinkEngine
) {

}


fun interface EngineTrackPredicate {
    fun evaluate(input: Input): Boolean
}

class Input(val url: StdUrl, val referrer: AndroidAppPackage?)

class TrackSelector(tracks: List<EngineTrack>) {
    private val tracks = tracks.sortedBy { it.position }

    fun find(input: Input): EngineTrack? {
        val track = tracks.firstOrNull { it.predicate.evaluate(input) }
        return track
    }
}
