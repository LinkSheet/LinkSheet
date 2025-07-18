package fe.linksheet.experiment.engine.resolver.followredirects

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.prop
import fe.linksheet.DatabaseTestRule
import fe.linksheet.experiment.engine.resolver.ResolveOutput
import fe.linksheet.experiment.engine.rule.withTestRunContext
import fe.linksheet.module.database.entity.cache.UrlEntry
import fe.linksheet.module.repository.CacheRepository
import fe.std.result.success
import fe.std.time.unixMillisOf
import fe.std.uri.toStdUrlOrThrow
import kotlinx.coroutines.test.runTest
import fe.linksheet.testlib.core.JunitTest
import fe.linksheet.testlib.core.BaseUnitTest
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.intArrayOf

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class FollowRedirectsLinkResolverTest : BaseUnitTest  {
    companion object {
        private val SHORT_URL = "https://t.co/JvpSaTXZDi".toStdUrlOrThrow()
        private const val RESOLVED_URL = "https://grumpy.website"
    }

    @get:Rule
    private val rule = DatabaseTestRule(applicationContext)

    private val cacheRepository by lazy {
        CacheRepository(
            rule.database.htmlCacheDao(),
            rule.database.previewCacheDao(),
            rule.database.resolvedUrlCacheDao(),
            rule.database.resolveTypeDao(),
            rule.database.urlEntryDao(),
            now = { unixMillisOf(2025) }
        )
    }

    private val source = FollowRedirectsSource {
        FollowRedirectsResult.LocationHeader(RESOLVED_URL).success
    }

    @org.junit.Test
    fun test() = runTest {
        val resolver = FollowRedirectsLinkResolver(
            source = source,
            cacheRepository = cacheRepository,
            allowDarknets = { false },
            allowNonPublic = { false },
            followOnlyKnownTrackers = { false },
            useLocalCache = { true }
        )

        val result = withTestRunContext(resolver) { it.runStep(SHORT_URL) }
        assertThat(result)
            .isNotNull()
            .prop(ResolveOutput::url)
            .transform { it.toString() }
            .isEqualTo(RESOLVED_URL)

        val entry = rule.database.urlEntryDao().getUrlEntry(SHORT_URL.toString())
        assertThat(entry)
            .isNotNull()
            .all {
                prop(UrlEntry::url).isEqualTo(SHORT_URL.toString())
                prop(UrlEntry::timestamp).isEqualTo(unixMillisOf(2025))
            }
    }
}
