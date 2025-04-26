package fe.linksheet.experiment.engine.rule

import assertk.Assert
import assertk.assertThat
import fe.linksheet.DatabaseTest
import fe.linksheet.experiment.engine.ContextualEngineResult
import fe.linksheet.experiment.engine.EngineLogger
import fe.linksheet.experiment.engine.EngineResult
import fe.linksheet.experiment.engine.context.EngineRunContext
import fe.linksheet.experiment.engine.step.EngineStepId
import fe.linksheet.experiment.engine.step.StepResult
import fe.linksheet.experiment.engine.modifier.LinkModifier
import fe.std.uri.StdUrl
import org.junit.After

abstract class BaseRuleEngineTest : DatabaseTest() {
    inline fun <reified T> createTestEngineLogger() = object : EngineLogger(T::class.simpleName!!) {
        override fun debug(message: () -> String) {
            println(message())
        }
    }

    fun assertResult(result: ContextualEngineResult): Assert<EngineResult> {
        return assertThat(result.second)
    }

    @After
    override fun stop() {
        println("[BaseLinkEngineTest] stop")
        super.stop()
    }
}

@Suppress("TestFunctionName")
fun TestLinkModifier(
    id: EngineStepId,
    block: suspend EngineRunContext.(StdUrl) -> StepTestResult? = { null },
): LinkModifier<StepTestResult> {
    return object : LinkModifier<StepTestResult> {
        override val id = id
        override suspend fun warmup() {}
        override suspend fun EngineRunContext.runStep(url: StdUrl): StepTestResult? {
            return block(url)
        }
    }
}

data class StepTestResult(override val url: StdUrl) : StepResult

object TestEngineRunContext : EngineRunContext {

}

suspend fun <T, R> withTestRunContext(it: T, block: suspend T.(EngineRunContext) -> R): R {
    return it.block(TestEngineRunContext)
}
