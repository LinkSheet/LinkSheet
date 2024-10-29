package fe.linksheet

import android.os.Build
import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.google.gson.GsonBuilder
import fe.gson.typeadapter.ExtendedTypeAdapter
import fe.linksheet.module.statistic.LastVersionService
import fe.linksheet.util.BuildInfo
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.Test

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
class StatisticsServiceTest : KoinTest {
    private val gson = GsonBuilder().apply {
        disableHtmlEscaping()
        ExtendedTypeAdapter.register(this)
    }.create()

    @Test
    fun `empty input`(): Unit = runBlocking {
        val buildInfo = BuildInfo("1", 1, "2024-10-29 12:00:00", "foss")
        val service = LastVersionService(gson, buildInfo)

        val expected = """[{"version_name":"1","version_code":1,"built_at":"2024-10-29 12:00:00","flavor":"foss"}]"""

        assertAll {
            assertThat(service.handleVersions(null)).isEqualTo(expected)
            assertThat(service.handleVersions("")).isEqualTo(expected)
            assertThat(service.handleVersions("[]")).isEqualTo(expected)
        }
    }

    @Test
    fun `same version`(): Unit = runBlocking {
        val buildInfo = BuildInfo("1", 1, "2024-10-29 12:00:00", "foss")
        val service = LastVersionService(gson, buildInfo)

        assertThat(
            service.handleVersions(
                """
                [{"version_name":"1","version_code":1,"built_at":"2024-10-29 12:00:00","flavor":"foss"}]
                """.trimIndent()
            )
        ).isNull()
    }

    @Test
    fun `new version`(): Unit = runBlocking {
        val buildInfo = BuildInfo("2", 2, "2024-10-30 12:00:00", "foss")
        val service = LastVersionService(gson, buildInfo)

        val v1 = """{"version_name":"1","version_code":1,"built_at":"2024-10-29 12:00:00","flavor":"foss"}"""
        val v2 = """{"version_name":"2","version_code":2,"built_at":"2024-10-30 12:00:00","flavor":"foss"}"""

        assertThat(
            service.handleVersions("[$v1]")
        ).isEqualTo("[$v1,$v2]")
    }

    @After
    fun teardown() = stopKoin()
}
