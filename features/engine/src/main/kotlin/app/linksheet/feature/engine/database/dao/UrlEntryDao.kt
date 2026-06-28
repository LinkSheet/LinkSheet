package app.linksheet.feature.engine.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.api.database.UserDataDao
import app.linksheet.feature.engine.database.entity.UrlEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface UrlEntryDao : BaseDao<UrlEntry>, UserDataDao {
    @Query("SELECT * FROM ${UrlEntry.TABLE_NAME}")
    override fun getAll(): Flow<List<UrlEntry>>
    @Query("DELETE FROM ${UrlEntry.TABLE_NAME}")
    override suspend fun deleteAll()
    @Query("SELECT * FROM ${UrlEntry.TABLE_NAME} WHERE url = :url ORDER BY timestamp LIMIT 1")
    suspend fun getUrlEntry(url: String): UrlEntry?
}
