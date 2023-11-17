package fe.linksheet.composable.util

import android.app.Activity
import android.content.ClipboardManager
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import fe.android.compose.dialog.helper.OnClose
import fe.gson.dsl.jsonArray
import fe.gson.dsl.jsonObject
import fe.gson.globalGsonModule
import fe.linksheet.R
import fe.linksheet.extension.android.setText
import fe.linksheet.module.log.LogEntry
import fe.linksheet.module.preference.AppPreferenceRepository
import fe.linksheet.module.preference.AppPreferences
import fe.linksheet.module.viewmodel.util.LogViewCommon
import fe.linksheet.util.Results
import java.io.FileOutputStream
import java.time.LocalDateTime

@Composable
fun ExportSettingsDialog(
    preferenceRepository: AppPreferenceRepository,
    close: OnClose<Unit>,
) {
    val historyFlag = false

    val context = LocalContext.current
    val contentResolver = context.contentResolver

    var includeLogHashKey by remember { mutableStateOf(false) }
    var includeHistory by remember { mutableStateOf(false) }

    val fileSelectedLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            if (it.resultCode == Activity.RESULT_OK) {
                val preferences = preferenceRepository.dumpPreferences(
                    if (!includeLogHashKey) listOf(AppPreferences.logKey) else listOf()
                )

                val fileContent = jsonObject {
                    "preferences" += AppPreferences.toJsonArray(preferences)
                }

                it.data?.data?.let { uri ->
                    contentResolver.openFileDescriptor(uri, "w")?.use { pfd ->
                        FileOutputStream(pfd.fileDescriptor).bufferedWriter().use { fos ->
                            fos.write(preferenceRepository.gson.toJson(fileContent))
                        }
                    }
                }
            }
        }
    )

    DialogColumn {
        HeadlineText(headlineId = R.string.export_settings)
        DialogSpacer()

        Text(text = stringResource(id = R.string.export_include_recommendation))

        CheckboxRow(
            checked = includeLogHashKey,
            onClick = { includeLogHashKey = !includeLogHashKey },
            textId = R.string.include_log_hash_key
        )

        if (historyFlag) {
            Spacer(modifier = Modifier.height(5.dp))

            CheckboxRow(
                checked = includeHistory,
                onClick = { includeHistory = !includeHistory },
                textId = R.string.include_history
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        LinkableTextView(
            id = R.string.export_privacy,
            style = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        BottomRow {
            TextButton(
                onClick = {
                    fileSelectedLauncher.launch(
                        Intent(Intent.ACTION_CREATE_DOCUMENT)
                            .addCategory(Intent.CATEGORY_OPENABLE)
                            .putExtra(
                                Intent.EXTRA_TITLE,
                                context.getString(R.string.export_file_name, LocalDateTime.now())
                            ).apply {
                                type = "application/json"
                            }
                    )
                }
            ) {
                Text(text = stringResource(id = R.string.export_to_file))
            }
        }
    }
}