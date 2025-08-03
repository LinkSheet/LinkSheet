@file:OptIn(ExperimentalUuidApi::class)

package fe.linksheet.experiment.engine

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.prop
import fe.linksheet.experiment.engine.rule.EmptyLinkEngine
import fe.linksheet.testlib.core.BaseUnitTest
import fe.std.uri.toStdUrlOrThrow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

//@RunWith(AndroidJUnit4::class)
//@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class EngineScenarioTest : BaseUnitTest {
    private val dispatcher = StandardTestDispatcher()

    @org.junit.Test
    fun test() = runTest(dispatcher) {
        val scenario1 = EngineScenario(
            id = Uuid.random(),
            position = 0,
            predicate = { false },
            engine = EmptyLinkEngine
        )

        val scenario2 = EngineScenario(
            id = Uuid.NIL,
            position = 1,
            predicate = { it.url.host == "linksheet.app" },
            engine = EmptyLinkEngine
        )

        val selector = ScenarioSelector(listOf(scenario1, scenario2))
        val input = EngineScenarioInput("https://linksheet.app/test".toStdUrlOrThrow(), null)
        val scenario = selector.findScenario(input)

        assertThat(scenario)
            .isNotNull()
            .prop(EngineScenario::id)
            .isEqualTo(Uuid.NIL)
    }
}
