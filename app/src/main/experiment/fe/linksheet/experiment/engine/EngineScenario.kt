package fe.linksheet.experiment.engine

import fe.linksheet.experiment.engine.context.EngineRunContext
import fe.linksheet.util.AndroidAppPackage
import fe.std.uri.StdUrl
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

class ScenarioSelector(scenarios: List<EngineScenario>) {
    private val scenarios = scenarios.sortedBy { it.position }

    fun findScenario(input: EngineScenarioInput): EngineScenario? {
        val scenario = scenarios.firstOrNull { it.matches(input) }
        return scenario
    }
}
