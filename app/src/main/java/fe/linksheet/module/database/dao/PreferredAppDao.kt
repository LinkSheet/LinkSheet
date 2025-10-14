package fe.linksheet.module.database.dao

import androidx.room.Dao
import androidx.room.Query
import app.linksheet.api.database.BaseDao
import fe.linksheet.module.database.entity.PreferredApp
import kotlinx.coroutines.flow.Flow

@Dao
interface PreferredAppDao : BaseDao<PreferredApp> {
    @Query("SELECT * FROM openwith WHERE alwaysPreferred = 1")
    fun getAllAlwaysPreferred(): Flow<List<PreferredApp>>

    @Query("SELECT * FROM openwith WHERE host = :host")
    fun getByHost(host: String): Flow<PreferredApp?>

    @Query("SELECT * FROM openwith WHERE packageName = :packageName")
    fun getByPackageName(packageName: String): Flow<List<PreferredApp>>

    @Query("DELETE FROM openwith WHERE host = :host")
    suspend fun deleteByHost(host: String)

    @Query("DELETE FROM openwith WHERE host = :host AND packageName = :packageName")
    suspend fun deleteByHostAndPackageName(host: String, packageName: String)

    @Query("DELETE FROM openwith WHERE packageName = :packageName")
    suspend fun deleteByPackageName(packageName: String)

    @Query("DELETE FROM openwith WHERE packageName IN (:packageNames)")
    suspend fun deleteByPackageName(packageNames: Set<String>)
}
