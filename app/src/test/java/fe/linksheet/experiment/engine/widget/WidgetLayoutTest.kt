package fe.linksheet.experiment.engine.widget

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.testing.fake.PackageInfoFakes
import app.linksheet.testing.util.toActivityAppInfo
import fe.linksheet.UnitTest
import fe.linksheet.experiment.engine.slot.AppRoleId
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
internal class WidgetLayoutTest : UnitTest {
    @Test
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
