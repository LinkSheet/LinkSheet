package fe.linksheet.module.resolver

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import fe.embed.resolve.EmbedResolver
import fe.embed.resolve.loader.BundledEmbedResolveConfigLoader
import fe.linksheet.testlib.core.BaseUnitTest
import fe.linksheet.testlib.core.JunitTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.intArrayOf

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class ImprovedIntentResolverTest : BaseUnitTest {

    @org.junit.Test
    fun testBundledEmbedResolving() {
        val config = BundledEmbedResolveConfigLoader.load().getOrNull()!!
        val resolved = EmbedResolver(config).resolve("https://fxtwitter.com/GrapheneOS/status/1805591682013876245")

        assertThat(resolved).isEqualTo("https://twitter.com/GrapheneOS/status/1805591682013876245")
    }
}
