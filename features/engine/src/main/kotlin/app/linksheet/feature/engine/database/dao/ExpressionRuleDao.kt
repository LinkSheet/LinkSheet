package app.linksheet.feature.engine.database.dao

import androidx.room3.Dao
import app.linksheet.api.database.BaseDao
import app.linksheet.feature.engine.database.entity.ExpressionRule

@Dao
interface ExpressionRuleDao : BaseDao<ExpressionRule> {
}
