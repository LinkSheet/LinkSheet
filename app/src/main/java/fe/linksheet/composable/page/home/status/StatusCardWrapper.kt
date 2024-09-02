package fe.linksheet.composable.page.home.status

import android.app.Activity
import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import fe.android.compose.system.rememberSystemService
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.util.AndroidVersion

@Composable
fun StatusCardWrapper(
    isDefaultBrowser: Boolean,
    updateDefaultBrowser: () -> Unit,
    launchIntent: (MainViewModel.SettingsIntent) -> Unit,
) {
    if (AndroidVersion.AT_LEAST_API_29_Q) {
        val intent = rememberRequestBrowserIntent()
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = {
                if (it.resultCode == Activity.RESULT_OK) {
                    updateDefaultBrowser()
                }
            }
        )

        StatusCard(
            isDefaultBrowser = isDefaultBrowser,
            launchIntent = launchIntent,
            onSetAsDefault = { launcher.launch(intent) }
        )
    } else {
        StatusCard(
            isDefaultBrowser = isDefaultBrowser,
            launchIntent = launchIntent,
            onSetAsDefault = { launchIntent(MainViewModel.SettingsIntent.DefaultApps) }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
private fun rememberRequestBrowserIntent(): Intent {
    val roleManager = rememberSystemService<RoleManager>()

    return remember(roleManager) {
        roleManager.createRequestRoleIntent(RoleManager.ROLE_BROWSER)
    }
}
