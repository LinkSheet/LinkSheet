package fe.linksheet.experiment.engine.resolver.followredirects

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
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
internal class FollowRedirectsLinkResolverTest : DatabaseTest() {
    companion object {
        private const val SHORT_URL = "https://t.co/JvpSaTXZDi"
        private const val RESOLVED_URL = "https://grumpy.website"
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
        FollowRedirectsResult.LocationHeader(RESOLVED_URL).success
    }

    @Test
    fun test() = runTest {
        val resolver = FollowRedirectsLinkResolver(
            source = source,
            cacheRepository = cacheRepository,
            allowDarknets = { false },
            followOnlyKnownTrackers = { false },
            localCache = { true }
        )

        val result = resolver.resolve(ResolveInput(SHORT_URL))
        assertThat(result)
            .isNotNull()
            .prop(ResolveOutput::url)
            .isEqualTo(RESOLVED_URL)

        val entry = database.urlEntryDao().getUrlEntry(SHORT_URL)
        assertThat(entry)
            .isNotNull()
            .all {
                prop(UrlEntry::url).isEqualTo(SHORT_URL)
                prop(UrlEntry::timestamp).isEqualTo(unixMillisOf(2025))
            }
    }
}
