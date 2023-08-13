package fe.linksheet.module.database.dao.resolver

import androidx.room.Dao
import androidx.room.Query
import fe.linksheet.module.database.dao.base.ResolverDao
import fe.linksheet.module.database.entity.resolver.ResolvedRedirect
import kotlinx.coroutines.flow.Flow

@Dao
interface ResolvedRedirectDao : ResolverDao<ResolvedRedirect> {
    @Query("SELECT * FROM resolved_redirect WHERE shortUrl = :inputUrl")
    override fun getForInputUrl(inputUrl: String): ResolvedRedirect?
}