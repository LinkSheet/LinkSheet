package fe.linksheet.module.systeminfo

import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import fe.linksheet.module.devicecompat.oneui.RealOneUiCompatProvider
import fe.linksheet.module.systeminfo.device.Device
import fe.linksheet.module.systeminfo.device.`Samsung A02s running Android 12`
import fe.linksheet.module.systeminfo.device.XiaomiRedmi2a
import fe.linksheet.testlib.core.BaseUnitTest
import fe.linksheet.testlib.core.RobolectricTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class OneUiCompatProviderTest : RobolectricTest {
    private fun Device.isProviderRequired(): Boolean {
        val service = SystemInfoService(this)
        val provider = RealOneUiCompatProvider(service)

        return provider.isRequired.value
    }

    @Test
    fun `compat required on stock samsung device`() {
        assertThat(`Samsung A02s running Android 12`.isProviderRequired()).isTrue()
    }

    @Test
    fun `compat not required on xiaomi device`() {
        assertThat(XiaomiRedmi2a.isProviderRequired()).isFalse()
    }
}
