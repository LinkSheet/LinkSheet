package app.linksheet.feature.app

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.feature.app.pkg.DefaultPackageIconLoader
import app.linksheet.feature.app.pkg.DefaultPackageIntentHandler
import app.linksheet.feature.app.pkg.DefaultPackageLabelService
import app.linksheet.feature.app.pkg.DefaultPackageLauncherService
import app.linksheet.feature.app.pkg.domain.DomainVerificationManagerCompat
import app.linksheet.feature.app.pkg.domain.VerificationBrowserState
import app.linksheet.feature.app.usecase.DomainVerificationUseCase
import app.linksheet.testing.fake.ImageFakes
import app.linksheet.testing.fake.PackageInfoFakes
import assertk.assertThat
import assertk.assertions.isNotNull
import fe.linksheet.testlib.core.BaseUnitTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
internal class DomainVerificationUseCaseTest : BaseUnitTest {

    @org.junit.Test
    fun test() {
        val domainVerificationManager = DomainVerificationManagerCompat {
            VerificationBrowserState
        }

        val domainVerificationPackageService = DomainVerificationUseCase(
            creator = AppInfoCreator(
                packageLabelService = DefaultPackageLabelService({ "" }, { "" }),
                packageLauncherService = DefaultPackageLauncherService { intent, flags -> emptyList() },
                packageIconLoader = DefaultPackageIconLoader(
                    ImageFakes.EmptyDrawable,
                    { _, _ -> ImageFakes.EmptyDrawable },
                    { ImageFakes.EmptyDrawable }
                ),
            ),
            domainVerificationManager = domainVerificationManager,
            packageIntentHandler = DefaultPackageIntentHandler(
                queryIntentActivities = { _, _ -> emptyList() },
                resolveActivity = { _, _ -> null },
                isLinkSheetCompat = { false },
                isSelf = { false },
                checkReferrerExperiment = { false }
            ),
            getApplicationInfoOrNull = { _, _ -> null },
            getInstalledPackages = { emptyList() },
        )

        val info = domainVerificationPackageService.createDomainVerificationAppInfo(PackageInfoFakes.MiBrowser.packageInfo)
        assertThat(info).isNotNull()
    }
}
