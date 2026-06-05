package app.linksheet.api.database

import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplace(item: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplace(items: List<T>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(items: List<T>): List<Long>

    @Delete
    suspend fun delete(item: T)

    @Query("")
    fun getAll(): Flow<List<T>>
}
