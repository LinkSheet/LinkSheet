package fe.linksheet.module.app

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.testing.ImageFakes
import app.linksheet.testing.PackageInfoFakes
import fe.linksheet.module.app.domain.DomainVerificationManagerCompat
import fe.linksheet.module.app.domain.VerificationBrowserState
import org.junit.After
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
internal class PackageInfoServiceTest {

    @Test
    fun test() {
        val domainVerificationManager = DomainVerificationManagerCompat {
            VerificationBrowserState
        }

        val pkgInfoService = PackageInfoService(
            domainVerificationManager = domainVerificationManager,
            packageLabelService = RealPackageInfoLabelService({ "" }, { "" }),
            packageInfoLauncherService = RealPackageInfoLauncherService { intent, flags -> emptyList() },
            packageInfoBrowserService = RealPackageInfoBrowserService { intent, flags -> emptyList() },
            queryIntentActivities = { intent, flags -> emptyList() },
            loadIcon = { info -> ImageFakes.EmptyDrawable },
            loadApplicationIcon = { appInfo -> ImageFakes.EmptyDrawable },
            getInstalledPackages = { emptyList() },
        )

        val info = pkgInfoService.createDomainVerificationAppInfo(PackageInfoFakes.MiBrowser.packageInfo)
    }

    @After
    fun teardown() = stopKoin()
}
