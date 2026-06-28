package app.linksheet.feature.engine.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey

@Entity(
    tableName = ScenarioExpression.TABLE_NAME,
    primaryKeys = [ScenarioExpression.COLUMN_SCENARIO_ID, ScenarioExpression.COLUMN_EXPRESSION_ID],
    foreignKeys = [
        ForeignKey(
            entity = Scenario::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf(ScenarioExpression.COLUMN_SCENARIO_ID)
        ),
        ForeignKey(
            entity = ExpressionRule::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf(ScenarioExpression.COLUMN_EXPRESSION_ID)
        )
    ]
)
data class ScenarioExpression(
    @ColumnInfo(name = COLUMN_SCENARIO_ID, index = true) val scenarioId: Long,
    @ColumnInfo(name = COLUMN_EXPRESSION_ID, index = true) val expressionId: Long,
) {
    companion object {
        const val TABLE_NAME = "scenario_expression"
        const val COLUMN_SCENARIO_ID = "scenarioId"
        const val COLUMN_EXPRESSION_ID = "expressionId"
    }
}
