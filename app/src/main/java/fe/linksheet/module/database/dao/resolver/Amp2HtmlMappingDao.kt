package fe.linksheet.module.database.dao.resolver

import androidx.room3.Dao
import androidx.room3.Query
import app.linksheet.api.database.BaseDao
import fe.linksheet.module.database.entity.resolver.Amp2HtmlMapping
import kotlinx.coroutines.flow.Flow

@Dao
interface Amp2HtmlMappingDao : BaseDao<Amp2HtmlMapping> {
    @Query("SELECT * FROM amp2html_mapping")
    override fun getAll(): Flow<List<Amp2HtmlMapping>>

    @Query("SELECT * FROM amp2html_mapping WHERE ampUrl = :inputUrl")
    fun getForInputUrl(inputUrl: String): Amp2HtmlMapping?
}
