@file:OptIn(ExperimentalUuidApi::class)

package app.linksheet.feature.engine.core

import app.linksheet.feature.engine.core.rule.EmptyLinkEngine
import assertk.assertThat
import assertk.assertions.isNotNull
import fe.linksheet.testlib.core.BaseUnitTest
import fe.std.uri.toStdUrlOrThrow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.uuid.ExperimentalUuidApi

//@RunWith(AndroidJUnit4::class)
//@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class EngineScenarioTest : BaseUnitTest {
    private val dispatcher = StandardTestDispatcher()

    @Test
    fun test() = runTest(dispatcher) {
        val scenario1 = EngineScenario(
            position = 0,
            predicate = { false },
            engine = EmptyLinkEngine
        )

        val scenario2 = EngineScenario(
            position = 1,
            predicate = { it.url.host == "linksheet.app" },
            engine = EmptyLinkEngine
        )

        val selector = ScenarioSelector(flowOf(listOf(scenario1, scenario2)), dispatcher)
        val input = EngineScenarioInput("https://linksheet.app/test".toStdUrlOrThrow(), null)
        val scenario = selector.findScenario(input)

        assertThat(scenario).isNotNull()
    }
}
