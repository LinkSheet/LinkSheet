@file:Suppress("TestFunctionName")

package app.linksheet.feature.engine.core.rule

import app.linksheet.feature.engine.core.ContextualEngineResult
import app.linksheet.feature.engine.core.EngineResult
import app.linksheet.feature.engine.core.LinkEngine
import app.linksheet.feature.engine.core.context.AppRole
import app.linksheet.feature.engine.core.context.AppRoleId
import app.linksheet.feature.engine.core.context.EngineExtra
import app.linksheet.feature.engine.core.context.EngineFlag
import app.linksheet.feature.engine.core.context.EngineRunContext
import app.linksheet.feature.engine.core.context.SealedRunContext
import app.linksheet.feature.engine.core.fetcher.ContextResult
import app.linksheet.feature.engine.core.fetcher.ContextResultId
import app.linksheet.feature.engine.core.modifier.LinkModifier
import app.linksheet.feature.engine.core.step.EngineStepId
import app.linksheet.feature.engine.core.step.StepResult
import assertk.Assert
import assertk.assertThat
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
