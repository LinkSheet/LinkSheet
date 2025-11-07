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
import kotlinx.coroutines.flow.Flow
import java.util.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ScenarioRepository internal constructor(
    private val scenarioDao: ScenarioDao,
    private val expressionRuleDao: ExpressionRuleDao,
    private val scenarioExpressionDao: ScenarioExpressionDao,
    private val serializer: BundleSerializer = BundleSerializer.Default
) {
    suspend fun createScenario(name: String): Scenario {
        val count = scenarioDao.getCount()
        val scenario = Scenario(id = Uuid.random(), name = name, position = count + 1, referrerApp = null)
        scenarioDao.insert(scenario)

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

    fun getById(uuid: UUID): Flow<Scenario> {
        return scenarioDao.getById(uuid)
    }

    suspend fun insertExpression(bundle: ExpressionBundle, type: ExpressionRuleType): ExpressionRule {
        val expression = ExpressionRule(bytes = serializer.encodeToByteArray(bundle), type = type)
        val id = expressionRuleDao.insertReturningId(expression)

        expression.id = id
        return expression
    }

    suspend fun insertScenarioExpression(id: UUID, expression: ExpressionRule) {
        scenarioExpressionDao.insert(ScenarioExpression(id, expression.id))
    }

    fun toBundle(expression: ExpressionRule): ExpressionBundle {
        return serializer.decodeFromByteArray(expression.bytes)
    }
}
