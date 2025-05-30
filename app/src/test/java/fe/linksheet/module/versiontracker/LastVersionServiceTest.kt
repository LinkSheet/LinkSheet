package fe.linksheet.module.versiontracker

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.google.gson.GsonBuilder
import fe.gson.typeadapter.ExtendedTypeAdapter
import fe.linksheet.module.systeminfo.BuildInfo
import fe.linksheet.testlib.core.BaseUnitTest
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

internal class LastVersionServiceTest : BaseUnitTest {

    companion object {
        private val gson = GsonBuilder().apply {
            disableHtmlEscaping()
            ExtendedTypeAdapter.register(this)
        }.create()

        private val buildInfoV1 = BuildInfo("1", 1, "2024-10-29 12:00:00", "foss")
        private val buildInfoV1Json = """
            {"version_name":"1","version_code":1,"built_at":"2024-10-29 12:00:00","flavor":"foss"}
        """.trimIndent()

        private val buildInfoV2Json = """
            {"version_name":"2","version_code":2,"built_at":"2024-10-30 12:00:00","flavor":"foss"}
        """.trimIndent()

        private val buildInfoV2 = BuildInfo("2", 2, "2024-10-30 12:00:00", "foss")
    }

    @Test
    fun `empty input`(): Unit = runBlocking {
        val service = LastVersionService(gson, buildInfoV1)
        val expected = "[${buildInfoV1Json}]"

        assertAll {
            assertThat(service.handleVersions(null)).isEqualTo(expected)
            assertThat(service.handleVersions("")).isEqualTo(expected)
            assertThat(service.handleVersions("[]")).isEqualTo(expected)
        }
    }

    @Test
    fun `same version`(): Unit = runBlocking {
        val service = LastVersionService(gson, buildInfoV1)
        val input = "[${buildInfoV1Json}]"

        assertThat(service.handleVersions(input)).isNull()
    }

    @Test
    fun `new version`(): Unit = runBlocking {
        val service = LastVersionService(gson, buildInfoV2)
        val input = "[${buildInfoV1Json}]"
        val expected = "[${buildInfoV1Json},${buildInfoV2Json}]"

        assertThat(service.handleVersions(input)).isEqualTo(expected)
    }
}
