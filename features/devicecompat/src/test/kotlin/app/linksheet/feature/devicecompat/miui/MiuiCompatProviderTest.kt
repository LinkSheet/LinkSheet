package app.linksheet.feature.devicecompat.miui

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.feature.devicecompat.util.BuildInfoFake
import app.linksheet.feature.devicecompat.util.RefineWrapperDummy
import app.linksheet.testing.fake.device.*
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import fe.linksheet.feature.systeminfo.SystemInfoService
import fe.linksheet.testlib.core.BaseUnitTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
internal class MiuiCompatProviderTest : BaseUnitTest {
    private fun Device.isProviderRequired(): Boolean {
        val service = SystemInfoService(this, buildInfo = BuildInfoFake.Info)
        val provider = RealMiuiCompatProvider(service, RefineWrapperDummy)

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
