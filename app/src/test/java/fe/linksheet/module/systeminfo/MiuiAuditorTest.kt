package fe.linksheet.module.systeminfo

import android.os.Build
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.tableOf
import fe.linksheet.RobolectricTest
import fe.linksheet.module.devicecompat.miui.MiuiAuditor
import fe.linksheet.module.systeminfo.device.Device
import fe.linksheet.module.systeminfo.device.Xiaomi11TPro_A13
import fe.linksheet.module.systeminfo.device.XiaomiMi5C
import fe.linksheet.module.systeminfo.device.XiaomiRedmi2a
import fe.linksheet.module.systeminfo.device.XiaomiRedmiNote13_A14
import fe.linksheet.module.systeminfo.device.XiaomiRedmiNote13_A15
import fe.linksheet.module.systeminfo.device.XiaomiRedmiNote3
import fe.linksheet.module.systeminfo.device.XiaomiRedmiNote4
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.ConscryptMode
import kotlin.test.Test

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
@ConscryptMode(ConscryptMode.Mode.OFF)
internal class MiuiAuditorTest : RobolectricTest {

    @Test
    fun test() {
        tableOf("device", "expectedDeviceInfo", "expectedMiui", "expectedFingerprint")
            .row<Device, DeviceInfo, MiuiAuditor.MiuiVersion, String>(
                XiaomiRedmi2a,
                DeviceInfo("4.4.4", "Xiaomi", "HM 2A"),
                MiuiAuditor.MiuiVersion("6", "V8"),
                "Xiaomi/full_lte26007/lte26007:4.4.4/KTU84Q/V8.0.1.0.KHLCNDG:user/release-keys"
            )
            .row(
                XiaomiRedmiNote3,
                DeviceInfo("5.1.1", "Xiaomi", "Redmi Note 3"),
                MiuiAuditor.MiuiVersion("6", "V8"),
                "Xiaomi/kenzo/kenzo:5.1.1/LMY47V/V8.0.7.0.LHOCNDG:user/release-keys"
            )
            .row(
                XiaomiRedmiNote4,
                DeviceInfo("7.0", "Xiaomi", "Redmi Note 4"),
                MiuiAuditor.MiuiVersion("6", "V8"),
                "xiaomi/mido/mido:7.0/NRD90M/V8.5.4.0.NCFMIED:user/release-keys"
            )
            .row(
                XiaomiMi5C,
                DeviceInfo("7.1.2", "Xiaomi", "MI 5C"),
                MiuiAuditor.MiuiVersion("6", "V8"),
                "Xiaomi/meri/meri:7.1.2/N2G47J/V8.5.3.0.0.NCJCNED:user/release-keys"
            )
            .row(
                Xiaomi11TPro_A13,
                DeviceInfo("13", "QUALCOMM", "missi system image for arm64"),
                MiuiAuditor.MiuiVersion("14", "V140"),
                "Xiaomi/cas/missi:13/TKQ1.221114.001/V14.0.2.0.TJJCNXM:user/release-keys"
            )
            .row(
                XiaomiRedmiNote13_A14,
                DeviceInfo("14", "Xiaomi", "mainline"),
                MiuiAuditor.MiuiVersion("816", "V816"),
                "Xiaomi/aurorapro/miproduct:14/UKQ1.231003.002/V816.0.21.0.UNACNXM:user/release-keys"
            )
            .row(
                XiaomiRedmiNote13_A15,
                DeviceInfo("15", "Xiaomi", "mainline"),
                MiuiAuditor.MiuiVersion("816", "V816"),
                "Xiaomi/aurora/miproduct:15/AQ3A.240627.003/OS2.0.2.0.VNAEUXM:user/release-keys"
            )
            .forAll { device, expectedDeviceInfo, expectedMiui, expectedFingerprint ->
                val infoService = SystemInfoService(device)

                val auditor = MiuiAuditor(infoService)
                val audit = auditor.audit(context)

                assertThat(audit.deviceInfo).isEqualTo(expectedDeviceInfo)
                assertThat(audit.miui).isEqualTo(expectedMiui)
                assertThat(audit.fingerprint).isEqualTo(expectedFingerprint)
            }
    }
}
