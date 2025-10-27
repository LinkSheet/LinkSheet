package app.linksheet.feature.scenario

import androidx.room.Dao
import app.linksheet.api.database.BaseDao

@Dao
interface ScenarioDao : BaseDao<ScenarioEntity> {
}
