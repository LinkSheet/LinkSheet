@file:OptIn(ExperimentalTime::class)

package fe.linksheet.feature.wiki.database.repository

import fe.linksheet.extension.kotlin.nowMillis
import fe.linksheet.feature.wiki.database.dao.WikiCacheDao
import fe.linksheet.feature.wiki.database.entity.WikiCache
import fe.linksheet.util.CacheResult
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime

class WikiCacheRepository(
    val dao: WikiCacheDao,
    val clock: Clock,
) {
    suspend fun getCached(url: String, maxAge: Duration = 24.hours): CacheResult<WikiCache> {
        val now = clock.now()
        val after = now.minus(maxAge).toEpochMilliseconds()
        val cached = dao.getCachedText(url) ?: return CacheResult.Miss

        return when {
            cached.timestamp > after -> CacheResult.Hit(cached)
            else -> CacheResult.Stale(cached)
        }
    }

    suspend fun update(docCache: WikiCache, text: String) {
        val cache = docCache.copy(timestamp = clock.nowMillis(), text = text)
        insert(cache)
    }

    suspend fun insert(url: String, text: String) {
        val cache = WikiCache(url = url, timestamp = clock.nowMillis(), text = text)
        insert(cache)
    }

    suspend fun insert(docCache: WikiCache) {
        dao.insert(docCache)
    }
}
