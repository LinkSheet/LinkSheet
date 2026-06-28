package fe.linksheet.module.database.dao.resolver

import androidx.room3.Dao
import androidx.room3.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.api.database.UserDataDao
import fe.linksheet.module.database.entity.resolver.Amp2HtmlMapping
import kotlinx.coroutines.flow.Flow

@Dao
interface Amp2HtmlMappingDao : BaseDao<Amp2HtmlMapping>, UserDataDao {
    @Query("SELECT * FROM ${Amp2HtmlMapping.TABLE_NAME}")
    override fun getAll(): Flow<List<Amp2HtmlMapping>>

    @Query("DELETE FROM ${Amp2HtmlMapping.TABLE_NAME}")
    override suspend fun deleteAll()

    @Query("SELECT * FROM ${Amp2HtmlMapping.TABLE_NAME} WHERE ${Amp2HtmlMapping.COLUMN_AMP_URL} = :inputUrl")
    fun getForInputUrl(inputUrl: String): Amp2HtmlMapping?
}
