package com.tasomaniac.openwith.data

import androidx.room.Dao
import androidx.room.Query
import fe.linksheet.data.dao.base.BaseDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Dao
interface PreferredAppDao : BaseDao<PreferredApp> {
    @Query("SELECT * FROM openwith WHERE alwaysPreferred = 1")
    fun getAllAlwaysPreferred(): Flow<List<PreferredApp>>

    @Query("SELECT * FROM openwith WHERE host = :host")
    fun getByHost(host: String): Flow<PreferredApp?>

    @Query("DELETE FROM openwith WHERE host = :host")
    suspend fun deleteByHost(host: String)

    @Query("DELETE FROM openwith WHERE host = :host AND packageName = :packageName")
    suspend fun deleteByHostAndPackageName(host: String, packageName: String)

    @Query("DELETE FROM openwith WHERE packageName = :packageName")
    suspend fun deleteByPackageName(packageName: String)
}
