package fe.linksheet.module.systeminfo

import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import fe.linksheet.module.devicecompat.miui.RealMiuiCompatProvider
import fe.linksheet.module.systeminfo.device.Device
import fe.linksheet.module.systeminfo.device.XiaomiMi5C
import fe.linksheet.module.systeminfo.device.XiaomiRedmi2a
import fe.linksheet.module.systeminfo.device.XiaomiRedmiNote13_A14
import fe.linksheet.module.systeminfo.device.XiaomiRedmiNote7Pro_DroidxA14
import fe.linksheet.testlib.core.RobolectricTest
import fe.linksheet.testlib.core.JunitTest
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class MiuiCompatProviderTest : RobolectricTest {
    private fun Device.isProviderRequired(): Boolean {
        val service = SystemInfoService(this)
        val provider = RealMiuiCompatProvider(service)

        return provider.isRequired.value
    }

    @JunitTest
    fun `compat required on stock xiaomi device`() {
        arrayOf(XiaomiMi5C, XiaomiRedmi2a, XiaomiRedmiNote13_A14)
            .forEach {
                assertThat(it.isProviderRequired()).isTrue()
            }
    }

    @JunitTest
    fun `compat not required on custom rom xiaomi device`() {
        assertThat(XiaomiRedmiNote7Pro_DroidxA14.isProviderRequired()).isFalse()
    }
}
