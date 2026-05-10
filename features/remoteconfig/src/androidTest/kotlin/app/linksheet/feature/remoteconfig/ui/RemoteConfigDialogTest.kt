package app.linksheet.feature.remoteconfig.ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import de.mannodermaus.junit5.compose.ComposeContext
import de.mannodermaus.junit5.compose.createComposeExtension
import fe.android.compose.feedback.LocalHapticFeedbackInteraction
import fe.android.compose.feedback.PreviewHapticFeedbackInteraction
import fe.linksheet.testlib.core.BaseUnitTest
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalTestApi::class)
internal class RemoteConfigDialogTest : BaseUnitTest {
    @JvmField
    @RegisterExtension
    val extension = createComposeExtension()

    private fun ComposeContext.initUi(onChanged: (Boolean) -> Unit) {
        setContent {
            CompositionLocalProvider(LocalHapticFeedbackInteraction provides PreviewHapticFeedbackInteraction) {
                val remoteConfigDialog = rememberRemoteConfigDialog(
                    onChanged = onChanged
                )
                LaunchedEffect(Unit) {
                    remoteConfigDialog.open()
                }
            }
        }
    }

    @Test
    fun testEnableRemoteConfig() = extension.use {
        val onChanged: (Boolean) -> Unit = mockk(relaxed = true)
        initUi(onChanged)

        waitForIdle()
        onNodeWithTag(REMOTE_CONFIG_DIALOG__ENABLE_TEST_TAG)
            .assertExists()
            .performClick()
        waitForIdle()

        verify(exactly = 1) { onChanged(true) }
    }

    @Test
    fun testDisableRemoteConfig() = extension.use {
        val onChanged: (Boolean) -> Unit = mockk(relaxed = true)
        initUi(onChanged)

        waitForIdle()
        onNodeWithTag(REMOTE_CONFIG_DIALOG__DISABLE_TEST_TAG)
            .assertExists()
            .performClick()
        waitForIdle()

        verify(exactly = 1) { onChanged(false) }
    }
}
