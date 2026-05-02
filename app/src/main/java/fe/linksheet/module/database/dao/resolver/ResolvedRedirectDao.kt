package fe.linksheet.module.database.dao.resolver

import androidx.room3.Dao
import androidx.room3.Query
import fe.linksheet.module.database.dao.base.ResolverDao
import fe.linksheet.module.database.entity.resolver.ResolvedRedirect

@Dao
interface ResolvedRedirectDao : ResolverDao<ResolvedRedirect> {
    @Query("SELECT * FROM resolved_redirect WHERE shortUrl = :inputUrl")
    override fun getForInputUrl(inputUrl: String): ResolvedRedirect?
}
