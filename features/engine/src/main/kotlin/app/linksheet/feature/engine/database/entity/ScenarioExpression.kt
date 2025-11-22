package app.linksheet.feature.engine.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "scenario_expression", primaryKeys = ["scenarioId", "expressionId"],
    foreignKeys = [
        ForeignKey(entity = Scenario::class, parentColumns = arrayOf("id"), childColumns = arrayOf("scenarioId")),
        ForeignKey(entity = ExpressionRule::class, parentColumns = arrayOf("id"), childColumns = arrayOf("expressionId"))
    ]
)
data class ScenarioExpression(
    @ColumnInfo(index = true) val scenarioId: Long,
    @ColumnInfo(index = true) val expressionId: Long,
)
