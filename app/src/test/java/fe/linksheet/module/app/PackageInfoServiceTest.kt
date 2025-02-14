package fe.linksheet.module.app

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.testing.fake.ImageFakes
import app.linksheet.testing.fake.PackageInfoFakes
import fe.linksheet.UnitTest
import fe.linksheet.module.app.`package`.DefaultPackageBrowserService
import fe.linksheet.module.app.`package`.DefaultPackageIconLoader
import fe.linksheet.module.app.`package`.DefaultPackageLabelService
import fe.linksheet.module.app.`package`.DefaultPackageLauncherService
import fe.linksheet.module.app.`package`.domain.DomainVerificationManagerCompat
import fe.linksheet.module.app.`package`.domain.VerificationBrowserState
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
internal class PackageInfoServiceTest : UnitTest {

    @Test
    fun test() {
        val domainVerificationManager = DomainVerificationManagerCompat {
            VerificationBrowserState
        }

        val pkgInfoService = PackageService(
            domainVerificationManager = domainVerificationManager,
            packageLabelService = DefaultPackageLabelService({ "" }, { "" }),
            packageLauncherService = DefaultPackageLauncherService { intent, flags -> emptyList() },
            packageBrowserService = DefaultPackageBrowserService { intent, flags -> emptyList() },
            packageIconLoader = DefaultPackageIconLoader(
                ImageFakes.EmptyDrawable,
                { _, _ -> ImageFakes.EmptyDrawable },
                { ImageFakes.EmptyDrawable }
            ),
            getInstalledPackages = { emptyList() },
        )

        val info = pkgInfoService.createDomainVerificationAppInfo(PackageInfoFakes.MiBrowser.packageInfo)
    }
}
