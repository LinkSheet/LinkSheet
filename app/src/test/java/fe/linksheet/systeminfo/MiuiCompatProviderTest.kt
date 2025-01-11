package fe.linksheet.systeminfo

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import fe.linksheet.module.devicecompat.miui.RealMiuiCompatProvider
import fe.linksheet.module.systeminfo.SystemInfoService
import fe.linksheet.systeminfo.device.*
import kotlin.test.Test

internal class MiuiCompatProviderTest {
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
