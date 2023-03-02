package fe.linksheet.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WhitelistedBrowsersDao {
    @Query("SELECT * FROM whitelisted_browser")
    fun getWhitelistedBrowsers(): List<WhitelistedBrowser>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(whitelistedBrowser: WhitelistedBrowser)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(whitelistedBrowsers: List<WhitelistedBrowser>)

    @Query("DELETE FROM whitelisted_browser WHERE packageName = :packageName")
    fun deleteByPackageName(packageName: String)
}