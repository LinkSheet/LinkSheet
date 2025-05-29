package fe.linksheet.resolver

import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import fe.embed.resolve.EmbedResolver
import fe.embed.resolve.loader.BundledEmbedResolveConfigLoader
import fe.linksheet.testlib.core.BaseUnitTest
import fe.linksheet.testlib.core.JunitTest
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class ImprovedIntentResolverTest : BaseUnitTest {

    @JunitTest
    fun testBundledEmbedResolving() {
        val config = BundledEmbedResolveConfigLoader.load().getOrNull()!!
        val resolved = EmbedResolver(config).resolve("https://fxtwitter.com/GrapheneOS/status/1805591682013876245")

        assertThat(resolved).isEqualTo("https://twitter.com/GrapheneOS/status/1805591682013876245")
    }
}
