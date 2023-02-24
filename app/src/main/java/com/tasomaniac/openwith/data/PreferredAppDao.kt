package com.tasomaniac.openwith.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PreferredAppDao {

    @Query("SELECT * FROM openwith WHERE alwaysPreferred = 1")
    fun allPreferredApps(): List<PreferredApp>

    @Query("SELECT * FROM openwith WHERE host = :host")
    fun preferredAppByHost(host: String): PreferredApp?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(preferredApp: PreferredApp)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(preferredApps: List<PreferredApp>)

    @Query("DELETE FROM openwith WHERE host = :host")
    fun deleteHost(host: String)
}
