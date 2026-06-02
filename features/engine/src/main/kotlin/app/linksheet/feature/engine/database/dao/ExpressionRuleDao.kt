package app.linksheet.feature.engine.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.feature.engine.database.entity.ExpressionRule
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpressionRuleDao : BaseDao<ExpressionRule> {
    @Query("SELECT * FROM expression_rule")
    override fun getAll(): Flow<List<ExpressionRule>>
}
