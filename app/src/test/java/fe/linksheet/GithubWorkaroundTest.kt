package fe.linksheet

import android.content.ComponentName
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.mp.KoinPlatformTools
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class GithubWorkaroundTest {
    @Test
    fun test() {
        val notFixedUri = BottomSheetViewModel.GithubWorkaround.tryFixUri(
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
            val fixedUri = BottomSheetViewModel.GithubWorkaround.tryFixUri(githubApp, Uri.parse(input))
            assertNotNull(fixedUri)
        }
    }

    @After
    fun teardown() = stopKoin()
}
