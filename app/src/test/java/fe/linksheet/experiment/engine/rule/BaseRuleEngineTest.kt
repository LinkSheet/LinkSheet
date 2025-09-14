@file:Suppress("TestFunctionName")

package fe.linksheet.experiment.engine.rule

import assertk.Assert
import assertk.assertThat
import fe.linksheet.experiment.engine.ContextualEngineResult
import fe.linksheet.experiment.engine.EngineResult
import fe.linksheet.experiment.engine.LinkEngine
import fe.linksheet.experiment.engine.context.AppRole
import fe.linksheet.experiment.engine.context.AppRoleId
import fe.linksheet.experiment.engine.context.EngineExtra
import fe.linksheet.experiment.engine.context.EngineFlag
import fe.linksheet.experiment.engine.context.EngineRunContext
import fe.linksheet.experiment.engine.context.SealedRunContext
import fe.linksheet.experiment.engine.fetcher.ContextResult
import fe.linksheet.experiment.engine.fetcher.ContextResultId
import fe.linksheet.experiment.engine.modifier.LinkModifier
import fe.linksheet.experiment.engine.step.EngineStepId
import fe.linksheet.experiment.engine.step.StepResult
import fe.linksheet.util.AndroidAppPackage
import fe.std.extension.emptyEnumSet
import fe.std.uri.StdUrl
import kotlinx.coroutines.CoroutineDispatcher
import java.util.*

//abstract class BaseRuleEngineTest(closeDb: Boolean = true) : DatabaseTest(closeDb) {

//    @After
//    override fun stop() {
//        println("[BaseLinkEngineTest] stop")
//        super.stop()
//    }
//}
fun LazyTestLinkEngine(dispatcher: CoroutineDispatcher, vararg rules: Rule<*, *>): Lazy<LinkEngine> {
    return lazy { TestLinkEngine(dispatcher, *rules) }
}

fun TestLinkEngine(dispatcher: CoroutineDispatcher, vararg rules: Rule<*, *>): LinkEngine {
    return LinkEngine(
        steps = listOf(
            TestLinkModifier(EngineStepId.Embed),
            TestLinkModifier(EngineStepId.ClearURLs) { StepTestResult(it) }
        ),
        rules = rules.toList(),
        dispatcher = dispatcher,
    )
}

fun assertResult(result: ContextualEngineResult): Assert<EngineResult> {
    return assertThat(result.second)
}

fun assertContext(result: ContextualEngineResult): Assert<SealedRunContext> {
    return assertThat(result.first)
}


fun TestLinkModifier(
    id: EngineStepId,
    block: suspend EngineRunContext.(StdUrl) -> StepTestResult? = { null },
): LinkModifier<StepTestResult> {
    return object : LinkModifier<StepTestResult> {
        override val enabled: () -> Boolean = { true }
        override val id = id
        override suspend fun warmup() {}
        override suspend fun EngineRunContext.runStep(url: StdUrl) = block(url)
        override fun toString() = "TestLinkModifier(id=$id)"
    }
}

val EmptyLinkEngine = LinkEngine(emptyList())

data class StepTestResult(override val url: StdUrl) : StepResult

object TestEngineRunContext : EngineRunContext {
    override val extras: Set<EngineExtra> = emptySet()
    override val flags: EnumSet<EngineFlag> = emptyEnumSet()
    override var allowCustomTab: Boolean? = null
    override val roles: MutableSet<AppRole> = mutableSetOf()

    override fun put(id: AppRoleId, appPackage: AndroidAppPackage): Boolean = true
    override fun <Result : ContextResult> put(id: ContextResultId<Result>, result: Result?) {}
    override fun <Result : ContextResult> confirm(fetcher: ContextResultId<Result>): Boolean = true
    override fun seal(): SealedRunContext = SealedRunContext(flags, roles, allowCustomTab, emptyMap())
}

suspend fun <T, R> withTestRunContext(it: T, block: suspend T.(EngineRunContext) -> R): R {
    return it.block(TestEngineRunContext)
}
