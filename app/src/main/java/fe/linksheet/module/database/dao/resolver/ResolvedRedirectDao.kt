package fe.linksheet.module.database.dao.resolver

import androidx.room3.Dao
import androidx.room3.Query
import app.linksheet.api.database.BaseDao
import fe.linksheet.module.database.entity.resolver.ResolvedRedirect
import kotlinx.coroutines.flow.Flow

@Dao
interface ResolvedRedirectDao : BaseDao<ResolvedRedirect> {
    @Query("SELECT * FROM resolved_redirect")
    override fun getAll(): Flow<List<ResolvedRedirect>>

    @Query("SELECT * FROM resolved_redirect WHERE shortUrl = :inputUrl")
    fun getForInputUrl(inputUrl: String): ResolvedRedirect?
}
