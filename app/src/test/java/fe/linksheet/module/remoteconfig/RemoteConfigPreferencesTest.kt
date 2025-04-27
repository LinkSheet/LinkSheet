package fe.linksheet.module.remoteconfig

import assertk.assertThat
import assertk.assertions.isEqualTo
import fe.linksheet.UnitTest
import fe.linksheet.util.LinkAssets
import kotlin.test.Test

internal class RemoteConfigPreferencesTest : UnitTest {
    private val linkAssets: LinkAssets = mapOf("hello" to "world", "foo" to "bar")
    private val serialized = """{"hello":"world","foo":"bar"}"""

    @Test
    fun `test unmap`() {
        val pref = RemoteConfigPreferences.linkAssets
        assertThat(pref.unmap(serialized)).isEqualTo(linkAssets)
    }

    @Test
    fun `test map`() {
        val pref = RemoteConfigPreferences.linkAssets
        assertThat(pref.map(linkAssets)).isEqualTo(serialized)
    }
}
