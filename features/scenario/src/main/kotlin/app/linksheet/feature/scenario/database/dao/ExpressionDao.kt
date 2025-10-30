package app.linksheet.feature.scenario.database.dao

import androidx.room.Dao
import app.linksheet.api.database.BaseDao
import app.linksheet.feature.scenario.database.entity.ExpressionEntity


@Dao
interface ExpressionDao : BaseDao<ExpressionEntity> {
}
