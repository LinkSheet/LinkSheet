package app.linksheet.feature.engine.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.RewriteQueriesToDropUnusedColumns
import app.linksheet.api.database.BaseDao
import app.linksheet.api.database.UserDataDao
import app.linksheet.feature.engine.database.entity.ExpressionRule
import app.linksheet.feature.engine.database.entity.Scenario
import app.linksheet.feature.engine.database.entity.ScenarioExpression
import kotlinx.coroutines.flow.Flow

@Dao
interface ScenarioExpressionDao : BaseDao<ScenarioExpression>, UserDataDao {
    @Query("SELECT * FROM ${ScenarioExpression.TABLE_NAME}")
    override fun getAll(): Flow<List<ScenarioExpression>>
    @Query("DELETE FROM ${ScenarioExpression.TABLE_NAME}")
    override suspend fun deleteAll()
    @RewriteQueriesToDropUnusedColumns
    @Query("""
        SELECT * 
        FROM ${Scenario.TABLE_NAME} s
            JOIN ${ScenarioExpression.TABLE_NAME} se ON s.id = se.scenarioId
            JOIN ${ExpressionRule.TABLE_NAME} e ON se.expressionId = e.id
        ORDER BY position ASC
    """)
    fun getAllScenarioExpressions(): Flow<Map<Scenario, List<ExpressionRule>>>

//    @RewriteQueriesToDropUnusedColumns
//    @Query(
//        """
//        SELECT *
//        FROM scenario s
//            JOIN ${ScenarioExpression.TABLE_NAME} se ON s.id = se.scenarioId
//            JOIN expression_rule e ON se.expressionId = e.id
//        WHERE s.id = :id
//        ORDER BY position ASC
//    """)
//    fun getScenarioExpressions(id: Long): Flow<ScenarioInfo?>
}

data class ScenarioInfo(
    val scenario: Scenario,
    val rules: List<ExpressionRule>
)
