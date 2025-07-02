package fe.linksheet.module.resolver.workaround

import android.content.ComponentName
import android.net.Uri
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import fe.linksheet.testlib.core.BaseUnitTest
import fe.linksheet.testlib.core.JunitTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
internal class GithubWorkaroundTest : BaseUnitTest {
    @org.junit.Test
    fun test() {
        val notFixedUri = GithubWorkaround.tryFixUri(
            ComponentName("not.github", "not.github"),
            Uri.parse("https://github.com/LinkSheet/LinkSheet/releases/latest")
        )

        assertNull(notFixedUri)

        val githubApp = ComponentName("com.github.android", "com.github.android.activities.DeepLinkActivity")

        val matchingStrings = setOf(
            "https://github.com/LinkSheet/LinkSheet/releases/latest",
            "http://github.com/LinkSheet/LinkSheet/releases/latest",
            "https://www.github.com/LinkSheet/LinkSheet/releases/latest",
            "https://www.github.com/LinkSheet/LinkSheet/releases/latest",
            "github://github.com/LinkSheet/LinkSheet/releases/latest",
            "github://www.github.com/LinkSheet/LinkSheet/releases/latest",
            "https://github.com/LinkSheet/LinkSheet/releases/latest/",
        )

        for (input in matchingStrings) {
            val fixedUri = GithubWorkaround.tryFixUri(githubApp, Uri.parse(input))
            assertNotNull(fixedUri)
        }
    }
}
