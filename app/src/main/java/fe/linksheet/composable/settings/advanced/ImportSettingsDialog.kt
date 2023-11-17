package fe.linksheet.composable.settings.advanced

import android.net.Uri
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import fe.android.compose.dialog.helper.OnClose
import fe.gson.extension.json.`object`.asArray
import fe.gson.extension.json.`object`.asStringOrNull
import fe.linksheet.R
import fe.linksheet.composable.util.BottomRow
import fe.linksheet.composable.util.DialogColumn
import fe.linksheet.composable.util.DialogSpacer
import fe.linksheet.composable.util.HeadlineText
import fe.linksheet.module.preference.AppPreferenceRepository
import java.io.FileInputStream

@Composable
fun ImportSettingsDialog(
    uri: Uri?,
    close: OnClose<Unit?>,
    preferenceRepository: AppPreferenceRepository,
) {
    val context = LocalContext.current
    val contentResolver = context.contentResolver

    DialogColumn {
        HeadlineText(headlineId = R.string.import_settings)
        DialogSpacer()

        Text(text = stringResource(id = R.string.import_settings_override_preferences))

        Spacer(modifier = Modifier.height(10.dp))

        BottomRow {
            TextButton(onClick = {
                uri?.let { uri ->
                    contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                        FileInputStream(pfd.fileDescriptor).bufferedReader().use { fis ->
                            val json = JsonParser.parseReader(fis)
                            val isLinkSheetPreferencesFile = json is JsonObject
                                    && json.keySet().size == 1
                                    && json.get("preferences") is JsonArray

                            if (isLinkSheetPreferencesFile) {
                                val preferences = (json as JsonObject).asArray("preferences")
                                val importMap = preferences.mapNotNull { preference ->
                                    if (preference !is JsonObject) return@mapNotNull null

                                    val name = preference.asStringOrNull("name")
                                    val value = preference.asStringOrNull("value")

                                    if (name == null || value == null) return@mapNotNull null
                                    name to value
                                }.toMap()

                                preferenceRepository.editor {
                                    preferenceRepository.importPreferences(importMap, this)
                                }

                                close(Unit)
                            }
                        }
                    }
                }
            }) {
                Text(text = stringResource(id = R.string.confirm_import))
            }
        }
    }
}

