@file:OptIn(ExperimentalUuidApi::class)

package app.linksheet.feature.engine.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.api.database.UserDataDao
import app.linksheet.feature.engine.database.entity.Scenario
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.ExperimentalUuidApi


@Dao
interface ScenarioDao : BaseDao<Scenario>, UserDataDao {
    @Query("SELECT * FROM ${Scenario.TABLE_NAME}")
    override fun getAll(): Flow<List<Scenario>>
    @Query("DELETE FROM ${Scenario.TABLE_NAME}")
    override suspend fun deleteAll()
    @Query("SELECT COUNT(id) FROM ${Scenario.TABLE_NAME}")
    fun getCount(): Int

    @Query("SELECT * FROM ${Scenario.TABLE_NAME} WHERE id = :id")
    fun getById(id: Long): Flow<Scenario>

    @Query("SELECT * FROM ${Scenario.TABLE_NAME} ORDER BY position ASC")
    fun getAllScenarios(): Flow<List<Scenario>>

    @Query("UPDATE ${Scenario.TABLE_NAME} SET position = :position WHERE id = :id")
    fun update(id: Long, position: Int)
}
