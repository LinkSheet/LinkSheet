package fe.linksheet.experiment.engine.widget

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.testing.fake.PackageInfoFakes
import app.linksheet.testing.fake.toActivityAppInfo
import fe.linksheet.experiment.engine.context.AppRoleId
import fe.linksheet.testlib.core.BaseUnitTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.intArrayOf

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
internal class WidgetLayoutTest : BaseUnitTest {

    @org.junit.Test
    fun test() {
        val layout = WidgetLayout(
            listOf(
                AppRoleSlotWidgetConfig(AppRoleId.Browser, 1),
                ListWidgetConfig(Order, 2),
            )
        )

        val resolved = PackageInfoFakes.allResolved
            .associate { it.toActivityAppInfo() to emptySet<AppRoleId>() }
        layout.create(resolved)
    }
}
