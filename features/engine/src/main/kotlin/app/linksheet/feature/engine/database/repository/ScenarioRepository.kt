@file:OptIn(ExperimentalUuidApi::class)

package app.linksheet.feature.engine.database.repository

import app.linksheet.feature.engine.database.dao.ExpressionRuleDao
import app.linksheet.feature.engine.database.dao.ScenarioDao
import app.linksheet.feature.engine.database.dao.ScenarioExpressionDao
import app.linksheet.feature.engine.database.entity.ExpressionRule
import app.linksheet.feature.engine.database.entity.ExpressionRuleType
import app.linksheet.feature.engine.database.entity.Scenario
import app.linksheet.feature.engine.database.entity.ScenarioExpression
import app.linksheet.feature.engine.eval.BundleSerializer
import app.linksheet.feature.engine.eval.ExpressionBundle
import app.linksheet.feature.engine.eval.ExpressionStringifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.uuid.ExperimentalUuidApi

class ScenarioRepository internal constructor(
    private val scenarioDao: ScenarioDao,
    private val expressionRuleDao: ExpressionRuleDao,
    private val scenarioExpressionDao: ScenarioExpressionDao,
    private val serializer: BundleSerializer = BundleSerializer.Default
) {
    suspend fun createScenario(name: String): Scenario {
        val count = scenarioDao.getCount()
        val scenario = Scenario(name = name, position = count + 1, referrerApp = null)
        val id = scenarioDao.insertReturningId(scenario)
        scenario.id = id
        return scenario
    }

    fun getAllScenarioExpressions(): Flow<Map<Scenario, List<ExpressionRule>>> {
        return scenarioExpressionDao.getAllScenarioExpressions()
    }

    fun getAllScenarios(): Flow<List<Scenario>> {
        return scenarioDao.getAllScenarios()
    }

    fun move(from: Scenario, to: Scenario): Boolean {
        scenarioDao.update(from.id, to.position)
        scenarioDao.update(to.id, from.position)
        return true
    }

    fun getScenarioExpressionsById(id: Long): Flow<Pair<Scenario, List<ExpressionRule>>?> {
        return scenarioExpressionDao.getScenarioExpressions(id).map {
            it.entries.firstOrNull()?.let {  entry -> entry.key to entry.value }
        }
    }

    fun getById(id: Long): Flow<Scenario> {
        return scenarioDao.getById(id)
    }

    fun toString(rule: ExpressionRule): String {
        val bundle = toBundle(rule)
        return ExpressionStringifier.stringify(bundle)
    }

    suspend fun insertExpression(bundle: ExpressionBundle, type: ExpressionRuleType): ExpressionRule {
        val expression = ExpressionRule(bytes = serializer.encodeToByteArray(bundle), type = type)
        val id = expressionRuleDao.insertReturningId(expression)
        expression.id = id
        return expression
    }

    suspend fun insertScenarioExpression(id: Long, expression: ExpressionRule) {
        scenarioExpressionDao.insert(ScenarioExpression(id, expression.id))
    }

    fun toBundle(expression: ExpressionRule): ExpressionBundle {
        return serializer.decodeFromByteArray(expression.bytes)
    }
}
