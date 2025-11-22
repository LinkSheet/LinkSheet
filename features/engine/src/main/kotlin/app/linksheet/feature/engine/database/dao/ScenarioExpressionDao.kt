package app.linksheet.feature.engine.database.dao

import androidx.room.Dao
import androidx.room.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.feature.engine.database.entity.ExpressionRule
import app.linksheet.feature.engine.database.entity.Scenario
import app.linksheet.feature.engine.database.entity.ScenarioExpression
import kotlinx.coroutines.flow.Flow

@Dao
interface ScenarioExpressionDao : BaseDao<ScenarioExpression> {
    @Query("""
        SELECT * 
        FROM scenario s
            JOIN scenario_expression se ON s.id = se.scenarioId
            JOIN expression_rule e ON se.expressionId = e.id
        ORDER BY position ASC
    """)
    fun getAllScenarioExpressions(): Flow<Map<Scenario, List<ExpressionRule>>>

    @Query(
        """
        SELECT * 
        FROM scenario s
            JOIN scenario_expression se ON s.id = se.scenarioId
            JOIN expression_rule e ON se.expressionId = e.id
        WHERE s.id = :id
        ORDER BY position ASC
    """
    )
    fun getScenarioExpressions(id: Long): Flow<Map<Scenario, List<ExpressionRule>>>
}
