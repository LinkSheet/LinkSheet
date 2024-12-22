package fe.linksheet.compat

import android.os.Build
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.tableOf
import com.google.gson.Gson
import fe.linksheet.module.build.BuildInfoService
import fe.linksheet.util.LinkSheetInfo
import fe.linksheet.module.build.SystemPropertiesBuildConstants
import fe.linksheet.util.device.xiaomi.MiuiAuditor
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.ConscryptMode
import kotlin.intArrayOf
import kotlin.test.Test

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
@ConscryptMode(ConscryptMode.Mode.OFF)
internal class MiuiCompatTest {

    private val gson = Gson()
    private val emptyAppOps = """{"10000":0,"10001":0,"10002":0,"10003":0,"10004":0,"10005":0,"10006":0,"10007":0,"10008":0,"10009":0,"10010":0,"10011":0,"10012":0,"10013":0,"10014":0,"10015":0,"10016":0,"10017":0,"10018":0,"10019":0,"10020":0,"10021":0,"10022":0,"10023":0,"10024":0,"10025":0,"10026":0,"10027":0,"10028":0,"10029":0,"10030":0,"10031":0,"10032":0,"10033":0,"10034":0,"10035":0,"10036":0,"10037":0,"10038":0,"10039":0,"10040":0}}"""


    @Test
    fun test() {
        val context = RuntimeEnvironment.getApplication().applicationContext

        val buildInfo = gson.toJson(LinkSheetInfo.buildInfo)
        println(buildInfo)

        val auditTests = tableOf("device", "result")
            .row<Device, String>(XiaomiMi5C, """{"buildInfo":$buildInfo,"deviceInfo":{"android_version":"14","manufacturer":"robolectric","model":"robolectric"},"fingerprint":"Xiaomi/meri/meri:7.1.2/N2G47J/V8.5.3.0.0.NCJCNED:user/release-keys","miui":{"code":"6","name":"V8"},"appOps":$emptyAppOps""")
            .row(XiaomiRedmiNote3, """{"buildInfo":$buildInfo,"deviceInfo":{"android_version":"14","manufacturer":"robolectric","model":"robolectric"},"fingerprint":"Xiaomi/kenzo/kenzo:5.1.1/LMY47V/V8.0.7.0.LHOCNDG:user/release-keys","miui":{"code":"6","name":"V8"},"appOps":$emptyAppOps""")
            .row(XiaomiRedmiNote4, """{"buildInfo":$buildInfo,"deviceInfo":{"android_version":"14","manufacturer":"robolectric","model":"robolectric"},"fingerprint":"xiaomi/mido/mido:7.0/NRD90M/V8.5.4.0.NCFMIED:user/release-keys","miui":{"code":"6","name":"V8"},"appOps":$emptyAppOps""")
            .row(Xiaomi11TPro_A13, """{"buildInfo":$buildInfo,"deviceInfo":{"android_version":"14","manufacturer":"robolectric","model":"robolectric"},"fingerprint":"Xiaomi/cas/missi:13/TKQ1.221114.001/V14.0.2.0.TJJCNXM:user/release-keys","miui":{"code":"14","name":"V140"},"appOps":""")
            .row(XiaomiRedmiNote13_A14, """{"buildInfo":$buildInfo,"deviceInfo":{"android_version":"14","manufacturer":"robolectric","model":"robolectric"},"fingerprint":"Xiaomi/aurorapro/miproduct:14/UKQ1.231003.002/V816.0.21.0.UNACNXM:user/release-keys","miui":{"code":"816","name":"V816"},"appOps":$emptyAppOps""")
            .row(XiaomiRedmiNote13_A15, """{"buildInfo":$buildInfo,"deviceInfo":{"android_version":"14","manufacturer":"robolectric","model":"robolectric"},"fingerprint":"Xiaomi/aurora/miproduct:15/AQ3A.240627.003/OS2.0.2.0.VNAEUXM:user/release-keys","miui":{"code":"816","name":"V816"},"appOps":$emptyAppOps""")

        auditTests.forAll { device, expected ->
            val build = SystemPropertiesBuildConstants(device)
            val infoService = BuildInfoService(device , build)

            val auditor = MiuiAuditor(infoService)
            val audit = auditor.audit(context)

            // TODO: fix
//            assertThat(gson.toJson(audit)).isEqualTo(expected)
        }
    }
}
