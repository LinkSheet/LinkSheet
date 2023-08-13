package fe.linksheet.module.database.dao.resolver

import androidx.room.Dao
import androidx.room.Query
import fe.linksheet.module.database.dao.base.ResolverDao
import fe.linksheet.module.database.entity.resolver.Amp2HtmlMapping
import kotlinx.coroutines.flow.Flow

@Dao
interface Amp2HtmlMappingDao : ResolverDao<Amp2HtmlMapping> {
    @Query("SELECT * FROM amp2html_mapping WHERE ampUrl = :inputUrl")
    override fun getForInputUrl(inputUrl: String): Amp2HtmlMapping?
}