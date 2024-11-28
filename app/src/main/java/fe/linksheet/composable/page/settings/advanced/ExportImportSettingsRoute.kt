package fe.linksheet.composable.page.settings.advanced

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fe.android.compose.dialog.helper.dialogHelper
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.list.item.default.DefaultTwoLineIconClickableShapeListItem
import fe.linksheet.R
import fe.linksheet.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.composable.page.settings.advanced.exportimport.ExportSettingsDialog
import fe.linksheet.composable.page.settings.advanced.exportimport.ImportPermissionRequiredDialog
import fe.linksheet.composable.page.settings.advanced.exportimport.ImportSettingsDialog
import fe.linksheet.composable.ui.LocalActivity
import fe.linksheet.module.preference.permission.PermissionBoundPreference
import fe.linksheet.module.viewmodel.ExportSettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExportImportSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: ExportSettingsViewModel = koinViewModel(),
) {
    val activity = LocalActivity.current

    val exportDialog = dialogHelper<Unit, Unit, Unit>(
        fetch = { },
        awaitFetchBeforeOpen = true,
        dynamicHeight = true
    ) { state, close ->
        ExportSettingsDialog(viewModel = viewModel)
    }

    val requestPermissionDialog =
        dialogHelper<List<PermissionBoundPreference>, List<PermissionBoundPreference>, Unit>(
            fetch = { it },
            awaitFetchBeforeOpen = true,
            dynamicHeight = true
        ) { state, close ->
            ImportPermissionRequiredDialog(activity = activity, permissions = state, close = close)
        }

    val confirmImportDialog = dialogHelper<Uri, Uri, Result<List<PermissionBoundPreference>>>(
        fetch = { it },
//        onClose = { result ->
//            if (result!!.isSuccess) {
//                requestPermissionDialog.open(result.getOrNull()!!)
//            }
//        },
        awaitFetchBeforeOpen = true,
        dynamicHeight = true
    ) { state, close ->
        ImportSettingsDialog(uri = state, close = close, viewModel = viewModel)
    }

    val importFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let {
                    confirmImportDialog.open(it)
                }
            }
        }
    )

    SaneScaffoldSettingsPage(
        headline = stringResource(id = R.string.export_import_settings),
        onBackPressed = onBackPressed
    ) {
        group(size = 2) {
            item(key = R.string.export) { padding, shape ->
                DefaultTwoLineIconClickableShapeListItem(
                    shape = shape,
                    padding = padding,
                    headlineContent = textContent(R.string.export),
                    supportingContent = textContent(R.string.export_explainer),
                    icon = Icons.Outlined.ImportExport.iconPainter,
                    onClick = {
                        exportDialog.open(Unit)
                    }
                )
            }

            item(key = R.string.import_headline) { padding, shape ->
                DefaultTwoLineIconClickableShapeListItem(
                    shape = shape,
                    padding = padding,
                    headlineContent = textContent(R.string.import_headline),
                    supportingContent = textContent(R.string.import_explainer),
                    icon = Icons.Outlined.ImportExport.iconPainter,
                    onClick = {
                        importFileLauncher.launch(viewModel.importIntent)
                    }
                )
            }
        }
    }
}

