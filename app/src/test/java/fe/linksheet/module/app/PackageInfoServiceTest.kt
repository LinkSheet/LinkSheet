package fe.linksheet.module.app

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.testing.fake.ImageFakes
import app.linksheet.testing.fake.PackageInfoFakes
import fe.linksheet.module.app.`package`.DefaultPackageIconLoader
import fe.linksheet.module.app.`package`.DefaultPackageIntentHandler
import fe.linksheet.module.app.`package`.DefaultPackageLabelService
import fe.linksheet.module.app.`package`.DefaultPackageLauncherService
import fe.linksheet.module.app.`package`.domain.DomainVerificationManagerCompat
import fe.linksheet.module.app.`package`.domain.VerificationBrowserState
import fe.linksheet.testlib.core.BaseUnitTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
internal class PackageInfoServiceTest : BaseUnitTest {

    @Test
    fun test() {
        val domainVerificationManager = DomainVerificationManagerCompat {
            VerificationBrowserState
        }

        val pkgInfoService = PackageService(
            domainVerificationManager = domainVerificationManager,
            packageLabelService = DefaultPackageLabelService({ "" }, { "" }),
            packageLauncherService = DefaultPackageLauncherService { intent, flags -> emptyList() },
            packageIntentHandler = DefaultPackageIntentHandler(
                queryIntentActivities = { _, _ -> emptyList() },
                resolveActivity = { _, _ -> null },
                isLinkSheetCompat = { false },
                isSelf = { false },
                checkReferrerExperiment = { false }
            ),
            packageIconLoader = DefaultPackageIconLoader(
                ImageFakes.EmptyDrawable,
                { _, _ -> ImageFakes.EmptyDrawable },
                { ImageFakes.EmptyDrawable }
            ),
            getApplicationInfoOrNull = { _, _ -> null },
            getInstalledPackages = { emptyList() },
        )

        val info = pkgInfoService.createDomainVerificationAppInfo(PackageInfoFakes.MiBrowser.packageInfo)
    }
}
