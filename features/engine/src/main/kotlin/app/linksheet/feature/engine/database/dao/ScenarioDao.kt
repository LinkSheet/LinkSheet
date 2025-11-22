@file:OptIn(ExperimentalUuidApi::class)

package app.linksheet.feature.engine.database.dao

import androidx.room.Dao
import androidx.room.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.feature.engine.database.entity.Scenario
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.ExperimentalUuidApi


@Dao
interface ScenarioDao : BaseDao<Scenario> {
    @Query("SELECT COUNT(id) FROM scenario")
    fun getCount(): Int

    @Query("SELECT * FROM scenario WHERE id = :id")
    fun getById(id: Long): Flow<Scenario>

    @Query("SELECT * FROM scenario ORDER BY position ASC")
    fun getAllScenarios(): Flow<List<Scenario>>

    @Query("UPDATE scenario SET position = :position WHERE id = :id")
    fun update(id: Long, position: Int)
}
