package app.linksheet.feature.scenario.database

import androidx.room.Dao
import app.linksheet.api.database.BaseDao


@Dao
interface ExpressionDao : BaseDao<ExpressionEntity> {
}
