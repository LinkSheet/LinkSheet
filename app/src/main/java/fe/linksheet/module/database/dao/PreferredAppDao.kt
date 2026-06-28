package fe.linksheet.module.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.api.database.UserDataDao
import fe.linksheet.module.database.entity.PreferredApp
import kotlinx.coroutines.flow.Flow

@Dao
interface PreferredAppDao : BaseDao<PreferredApp>, UserDataDao {
    @Query("SELECT * FROM ${PreferredApp.TABLE_NAME}")
    override fun getAll(): Flow<List<PreferredApp>>
    @Query("DELETE FROM ${PreferredApp.TABLE_NAME}")
    override suspend fun deleteAll()
    @Query("SELECT * FROM ${PreferredApp.TABLE_NAME} WHERE alwaysPreferred = 1")
    fun getAllAlwaysPreferred(): Flow<List<PreferredApp>>

    @Query("SELECT * FROM ${PreferredApp.TABLE_NAME} WHERE host = :host")
    fun getByHost(host: String): Flow<PreferredApp?>

    @Query("SELECT * FROM ${PreferredApp.TABLE_NAME} WHERE packageName = :packageName")
    fun getByPackageName(packageName: String): Flow<List<PreferredApp>>

    @Query("DELETE FROM ${PreferredApp.TABLE_NAME} WHERE host = :host")
    suspend fun deleteByHost(host: String)

    @Query("DELETE FROM ${PreferredApp.TABLE_NAME} WHERE host = :host AND packageName = :packageName")
    suspend fun deleteByHostAndPackageName(host: String, packageName: String)

    @Query("DELETE FROM ${PreferredApp.TABLE_NAME} WHERE packageName = :packageName")
    suspend fun deleteByPackageName(packageName: String)

    @Query("DELETE FROM ${PreferredApp.TABLE_NAME} WHERE packageName IN (:packageNames)")
    suspend fun deleteByPackageName(packageNames: Set<String>)
}
