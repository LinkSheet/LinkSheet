package app.linksheet.feature.app.usecase

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.linksheet.feature.app.core.*
import app.linksheet.feature.app.core.domain.DomainVerificationManagerCompat
import app.linksheet.feature.app.core.domain.VerificationBrowserState
import app.linksheet.testing.fake.ImageFakes
import app.linksheet.testing.fake.PackageInfoFakes
import assertk.assertThat
import assertk.assertions.isNotNull
import fe.linksheet.testlib.core.BaseUnitTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
internal class DomainVerificationUseCaseTest : BaseUnitTest {

    @Test
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
            ),
            getApplicationInfoOrNull = { _, _ -> null },
            getInstalledPackages = { emptyList() },
        )

        val info = domainVerificationPackageService.createDomainVerificationAppInfo(PackageInfoFakes.MiBrowser.packageInfo)
        assertThat(info).isNotNull()
    }
}
