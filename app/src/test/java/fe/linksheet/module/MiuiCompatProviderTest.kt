package fe.linksheet.module

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.testing.fake.device.*
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import fe.linksheet.BuildInfoFake
import fe.linksheet.feature.systeminfo.SystemInfoService
import fe.linksheet.module.devicecompat.miui.RealMiuiCompatProvider
import fe.linksheet.testlib.core.BaseUnitTest
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class MiuiCompatProviderTest : BaseUnitTest  {
    private fun Device.isProviderRequired(): Boolean {
        val service = SystemInfoService(this, buildInfo = BuildInfoFake.Info)
        val provider = RealMiuiCompatProvider(service)

        return provider.isRequired.value
    }

    @org.junit.Test
    fun `compat required on stock xiaomi device`() {
        arrayOf(XiaomiMi5C, XiaomiRedmi2a, XiaomiRedmiNote13_A14)
            .forEach {
                assertThat(it.isProviderRequired()).isTrue()
            }
    }

    @org.junit.Test
    fun `compat not required on custom rom xiaomi device`() {
        assertThat(XiaomiRedmiNote7Pro_DroidxA14.isProviderRequired()).isFalse()
    }
}
