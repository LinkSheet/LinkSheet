@file:OptIn(ExperimentalTime::class)

package app.linksheet.feature.wiki.database.repository

import app.linksheet.feature.wiki.database.dao.WikiCacheDao
import app.linksheet.feature.wiki.database.entity.WikiCache
import fe.linksheet.extension.kotlin.nowMillis
import fe.linksheet.util.CacheResult
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime

class WikiCacheRepository internal constructor(
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

    suspend fun update(cache: WikiCache, text: String) {
        val cache = cache.copy(timestamp = clock.nowMillis(), text = text)
        insert(cache)
    }

    suspend fun insert(url: String, text: String) {
        val cache = WikiCache(url = url, timestamp = clock.nowMillis(), text = text)
        insert(cache)
    }

    suspend fun insert(cache: WikiCache) {
        dao.insert(cache)
    }
}
