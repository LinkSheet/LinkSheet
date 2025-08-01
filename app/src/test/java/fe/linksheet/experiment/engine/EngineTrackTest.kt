@file:OptIn(ExperimentalUuidApi::class)

package fe.linksheet.experiment.engine

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.prop
import fe.linksheet.experiment.engine.rule.EmptyLinkEngine
import fe.linksheet.testlib.core.BaseUnitTest
import fe.std.uri.toStdUrlOrThrow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

//@RunWith(AndroidJUnit4::class)
//@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class EngineTrackTest : BaseUnitTest {
    private val dispatcher = StandardTestDispatcher()

    @org.junit.Test
    fun test() = runTest(dispatcher) {
        val track1 = EngineTrack(
            id = Uuid.random(),
            position = 0,
            predicate = { false },
            engine = EmptyLinkEngine
        )

        val track2 = EngineTrack(
            id = Uuid.NIL,
            position = 1,
            predicate = { it.url.host == "linksheet.app" },
            engine = EmptyLinkEngine
        )

        val selector = TrackSelector(listOf(track1, track2))
        val input = EngineTrackInput("https://linksheet.app/test".toStdUrlOrThrow(), null)
        val track = selector.findTrack(input)

        assertThat(track)
            .isNotNull()
            .prop(EngineTrack::id)
            .isEqualTo(Uuid.NIL)
    }
}
