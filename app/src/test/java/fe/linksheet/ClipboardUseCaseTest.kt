package fe.linksheet

import android.content.ClipboardManager
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import fe.composekit.extension.getSystemServiceOrThrow
import fe.linksheet.module.ClipboardUseCase
import fe.linksheet.module.preference.app.DefaultAppPreferenceRepository
import fe.linksheet.testlib.core.BaseUnitTest
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
class ClipboardUseCaseTest : BaseUnitTest {
    @org.junit.Test
    fun test() = runTest {
        val clipboardManager = applicationContext.getSystemServiceOrThrow<ClipboardManager>()
        val useCase = ClipboardUseCase(
            preferenceRepository = DefaultAppPreferenceRepository(applicationContext),
            clipboardManager = clipboardManager
        )
        useCase.contentFlow.test {

        }
    }
}
