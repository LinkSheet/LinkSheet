package fe.linksheet.module.repository.resolver

import fe.linksheet.module.database.dao.resolver.Amp2HtmlMappingDao
import fe.linksheet.module.database.entity.resolver.Amp2HtmlMapping
import kotlinx.coroutines.flow.Flow

class Amp2HtmlRepository(private val dao: Amp2HtmlMappingDao) {
    fun getAll(): Flow<List<Amp2HtmlMapping>> {
        return dao.getAll()
    }

    suspend fun insert(inputUrl: String, resolvedUrl: String): Amp2HtmlMapping {
        val item = Amp2HtmlMapping(inputUrl, resolvedUrl)
        dao.insert(item)
        return item
    }

    fun getForInputUrl(inputUrl: String): Pair<Amp2HtmlMapping, String?>? {
        val item = dao.getForInputUrl(inputUrl)
        return item?.let { it to it.canonicalUrl }
    }
}
