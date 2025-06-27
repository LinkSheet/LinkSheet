package fe.linksheet.module.remoteconfig

import assertk.assertThat
import assertk.assertions.isEqualTo
import fe.linksheet.testlib.core.BaseUnitTest
import fe.linksheet.testlib.core.JunitTest
import fe.linksheet.util.LinkAssets


internal class RemoteConfigPreferencesTest : BaseUnitTest {
    private val linkAssets: LinkAssets = mapOf("hello" to "world", "foo" to "bar")
    private val serialized = """{"hello":"world","foo":"bar"}"""

    @JunitTest
    fun `test unmap`() {
        val pref = RemoteConfigPreferences.linkAssets
        assertThat(pref.unmap(serialized)).isEqualTo(linkAssets)
    }

    @JunitTest
    fun `test map`() {
        val pref = RemoteConfigPreferences.linkAssets
        assertThat(pref.map(linkAssets)).isEqualTo(serialized)
    }
}
