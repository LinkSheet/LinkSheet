package app.linksheet.feature.engine.core

import app.linksheet.feature.engine.core.context.EngineRunContext
import fe.linksheet.util.AndroidAppPackage
import fe.std.uri.StdUrl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.shareIn
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class EngineScenario(
    val id: Uuid,
    val position: Int,
    private val predicate: EngineScenarioPredicate,
    private val engine: LinkEngine
) {
    fun matches(input: EngineScenarioInput): Boolean {
        return predicate.evaluate(input)
    }

    suspend fun run(url: StdUrl, context: EngineRunContext): ContextualEngineResult {
        return engine.process(url, context)
    }
}

fun interface EngineScenarioPredicate {
    fun evaluate(input: EngineScenarioInput): Boolean
}

data class EngineScenarioInput(val url: StdUrl, val referrer: AndroidAppPackage?)

class ScenarioSelector(
    scenarioFlow: Flow<List<EngineScenario>>,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private val scope = CoroutineScope(dispatcher)
    private val scenarioFlow = scenarioFlow.shareIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        replay = 1
    )

    suspend fun findScenario(input: EngineScenarioInput): EngineScenario? {
        val scenario = scenarioFlow.first().firstOrNull { it.matches(input) }
        return scenario
    }
}
