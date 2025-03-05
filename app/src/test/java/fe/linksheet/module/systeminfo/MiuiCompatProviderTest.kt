package fe.linksheet.module.systeminfo

import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import fe.linksheet.UnitTest
import fe.linksheet.module.devicecompat.miui.RealMiuiCompatProvider
import fe.linksheet.module.systeminfo.device.Device
import fe.linksheet.module.systeminfo.device.XiaomiMi5C
import fe.linksheet.module.systeminfo.device.XiaomiRedmi2a
import fe.linksheet.module.systeminfo.device.XiaomiRedmiNote13_A14
import fe.linksheet.module.systeminfo.device.XiaomiRedmiNote7Pro_DroidxA14
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
internal class MiuiCompatProviderTest : UnitTest {
    private fun Device.isProviderRequired(): Boolean {
        val service = SystemInfoService(this)
        val provider = RealMiuiCompatProvider(service)

        return provider.isRequired.value
    }

    @Test
    fun `compat required on stock xiaomi device`() {
        arrayOf(XiaomiMi5C, XiaomiRedmi2a, XiaomiRedmiNote13_A14)
            .forEach {
                assertThat(it.isProviderRequired()).isTrue()
            }
    }

    @Test
    fun `compat not required on custom rom xiaomi device`() {
        assertThat(XiaomiRedmiNote7Pro_DroidxA14.isProviderRequired()).isFalse()
    }
}
