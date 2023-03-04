package fe.linksheet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fe.linksheet.data.entity.ResolvedRedirect
import fe.linksheet.data.entity.WhitelistedBrowser

@Dao
interface ResolvedRedirectDao {
    @Query("SELECT * FROM resolved_redirect WHERE shortUrl = :shortUrl")
    fun getResolvedRedirectForShortUrl(shortUrl: String): ResolvedRedirect?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(resolvedRedirect: ResolvedRedirect)
}