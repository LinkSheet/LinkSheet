package fe.linksheet.experiment.engine.resolver.redirects

import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.prop
import fe.linksheet.DatabaseTest
import fe.linksheet.experiment.engine.resolver.ResolveInput
import fe.linksheet.experiment.engine.resolver.ResolveOutput
import fe.linksheet.module.database.entity.cache.UrlEntry
import fe.linksheet.module.repository.CacheRepository
import fe.std.result.success
import fe.std.time.unixMillisOf
import kotlinx.coroutines.runBlocking
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
internal class FollowRedirectsLinkResolverTest : DatabaseTest() {
    companion object {
        private const val shortUrl = "https://t.co/JvpSaTXZDi"
        private const val resolvedUrl = "https://grumpy.website"
    }

    private val cacheRepository by lazy {
        CacheRepository(
            database.htmlCacheDao(),
            database.previewCacheDao(),
            database.resolvedUrlCacheDao(),
            database.resolveTypeDao(),
            database.urlEntryDao(),
            now = { unixMillisOf(2025) }
        )
    }

    private val source = FollowRedirectsSource {
        FollowRedirectsResult.LocationHeader(resolvedUrl).success
    }

    @Test
    fun test() = runBlocking {
        val resolver = FollowRedirectsLinkResolver(
            source = source,
            cacheRepository = cacheRepository,
            allowDarknets = { false },
            localCache = { true }
        )

        val result = resolver.resolve(ResolveInput(shortUrl))
        assertThat(result)
            .isNotNull()
            .prop(ResolveOutput::url)
            .isEqualTo(resolvedUrl)

        val entry = database.urlEntryDao().getCacheEntry(resolvedUrl)
        assertThat(entry)
            .isNotNull()
            .all {
                prop(UrlEntry::url).isEqualTo(resolvedUrl)
                prop(UrlEntry::timestamp).isEqualTo(unixMillisOf(2025))
            }
    }
}
