package com.tasomaniac.openwith.data

import androidx.room.Dao
import androidx.room.Query
import fe.linksheet.data.dao.base.BaseDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Dao
interface PreferredAppDao : BaseDao<PreferredApp> {
//    @Query("SELECT * FROM openwith WHERE alwaysPreferred = 1")
//    fun getAllAlwaysPreferred(): Flow<List<PreferredApp>>

    fun getAllAlwaysPreferred() = flowOf(
        listOf(
            PreferredApp(1, "google.com", "com.android.chrome", "Lel", false),
            PreferredApp(2, "google2.com", "com.android.chrome", "Lel", false),
            PreferredApp(3, "google3.com", "org.mozilla.fennec_fdroid", "Lel2", false),
            PreferredApp(4, "google1.com", "com.android.chrome", "Lel", false),
            PreferredApp(5, "google.com", "org.mozilla.fennec_fdroid", "Lel2", false),
            PreferredApp(6, "google.com", "com.android.chrome", "Lel", false)
        )
    )


    @Query("SELECT * FROM openwith WHERE host = :host")
    suspend fun getByHost(host: String): PreferredApp?

    @Query("DELETE FROM openwith WHERE host = :host")
    suspend fun deleteByHost(host: String)

    @Query("DELETE FROM openwith WHERE host = :host AND packageName = :packageName")
    suspend fun deleteByHostAndPackageName(host: String, packageName: String)

    @Query("DELETE FROM openwith WHERE packageName = :packageName")
    suspend fun deleteByPackageName(packageName: String)
}
