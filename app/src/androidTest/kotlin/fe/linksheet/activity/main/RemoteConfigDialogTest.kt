package fe.linksheet.activity.main

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import de.mannodermaus.junit5.compose.createComposeExtension
import fe.linksheet.composable.page.settings.privacy.remoteconfig.REMOTE_CONFIG_DIALOG__ENABLE_TEST_TAG
import fe.linksheet.composable.page.settings.privacy.remoteconfig.rememberRemoteConfigDialog
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

    @Test
    fun testEnableRemoteConfig() = extension.use {
        val onChanged: (Boolean) -> Unit = mockk(relaxed = true)
        setContent {
            val remoteConfigDialog = rememberRemoteConfigDialog(
                onChanged = onChanged
            )
            LaunchedEffect(Unit) {
                remoteConfigDialog.open()
            }
        }

        onNodeWithTag(REMOTE_CONFIG_DIALOG__ENABLE_TEST_TAG)
            .assertExists()
            .performClick()

        verify(exactly = 1) { onChanged(true) }
    }

    @Test
    fun testCancel() = extension.use {

    }
}
