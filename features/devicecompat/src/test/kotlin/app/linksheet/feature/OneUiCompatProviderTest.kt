package app.linksheet.feature

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.feature.devicecompat.oneui.RealOneUiCompatProvider
import app.linksheet.testing.fake.device.Device
import app.linksheet.testing.fake.device.SamsungA02sAndroid12
import app.linksheet.testing.fake.device.XiaomiRedmi2a
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import fe.linksheet.feature.systeminfo.SystemInfoService
import fe.linksheet.testlib.core.BaseUnitTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class OneUiCompatProviderTest : BaseUnitTest  {
    private fun Device.isProviderRequired(): Boolean {
        val service = SystemInfoService(this, buildInfo = BuildInfoFake.Info)
        val provider = RealOneUiCompatProvider(service)

        return provider.isRequired.value
    }

    @Test
    fun `compat required on stock samsung device`() {
        assertThat(SamsungA02sAndroid12.isProviderRequired()).isTrue()
    }

    @Test
    fun `compat not required on xiaomi device`() {
        assertThat(XiaomiRedmi2a.isProviderRequired()).isFalse()
    }
}
