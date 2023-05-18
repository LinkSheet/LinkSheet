package fe.linksheet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fe.linksheet.data.dao.base.BaseDao
import fe.linksheet.data.entity.ResolvedRedirect

@Dao
interface ResolvedRedirectDao : BaseDao<ResolvedRedirect> {
    @Query("SELECT * FROM resolved_redirect WHERE shortUrl = :shortUrl")
    fun getForShortUrl(shortUrl: String): ResolvedRedirect?
}