package app.linksheet.feature.remoteconfig.service

import app.linksheet.feature.remoteconfig.preference.StoredRemotePreferences
import app.linksheet.feature.remoteconfig.util.LinkAssets
import assertk.assertThat
import assertk.assertions.isEqualTo
import fe.linksheet.testlib.core.BaseUnitTest
import org.junit.Test


internal class RemoteConfigPreferencesTest : BaseUnitTest {
    private val linkAssets: LinkAssets = mapOf("hello" to "world", "foo" to "bar")
    private val serialized = """{"hello":"world","foo":"bar"}"""

    @Test
    fun `test unmap`() {
        val pref = StoredRemotePreferences.linkAssets
        assertThat(pref.unmap(serialized)).isEqualTo(linkAssets)
    }

    @Test
    fun `test map`() {
        val pref = StoredRemotePreferences.linkAssets
        assertThat(pref.map(linkAssets)).isEqualTo(serialized)
    }
}
