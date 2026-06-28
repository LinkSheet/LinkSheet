package app.linksheet.feature.engine.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.api.database.UserDataDao
import app.linksheet.feature.engine.database.entity.ExpressionRule
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpressionRuleDao : BaseDao<ExpressionRule>, UserDataDao {
    @Query("SELECT * FROM ${ExpressionRule.TABLE_NAME}")
    override fun getAll(): Flow<List<ExpressionRule>>

    @Query("DELETE FROM ${ExpressionRule.TABLE_NAME}")
    override suspend fun deleteAll()
}
