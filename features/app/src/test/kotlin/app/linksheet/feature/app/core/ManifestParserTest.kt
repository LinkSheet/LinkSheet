package app.linksheet.feature.app.core

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import com.reandroid.archive.ArchiveBytes
import fe.linksheet.testlib.core.BaseUnitTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
internal class ManifestParserTest : BaseUnitTest {

    @org.junit.Test
    fun test() {
        val stream = ManifestParserTest::class.java.getResourceAsStream("/linksheet_compat-2025-12-11.apk")
        val bytes = ArchiveBytes(stream)
        val hosts = ManifestParser().parse(bytes)
        assertThat(hosts).containsExactlyInAnyOrder(
            "github.com",
            "reddit.com",
            "youtube.com",
            "www.youtube.com",
            "m.youtube.com",
            "youtu.be"
        )
    }
}
