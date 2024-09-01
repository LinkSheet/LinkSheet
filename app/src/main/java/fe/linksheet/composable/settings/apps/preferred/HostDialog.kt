package fe.linksheet.composable.settings.apps.preferred

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.android.compose.dialog.helper.OnClose
import fe.android.compose.dialog.helper.dialogHelper
import fe.linksheet.R
import fe.linksheet.composable.util.*
import fe.linksheet.extension.android.startPackageInfoActivity
import fe.linksheet.extension.compose.updateState
import fe.linksheet.extension.compose.updateStateFromResult
import fe.linksheet.module.viewmodel.PreferredAppSettingsViewModel
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.ui.HkGroteskFontFamily

data class HostDialogState(
    val displayActivityInfo: DisplayActivityInfo,
    val hosts: Collection<String> = mutableSetOf()
)

data class HostDialogCloseState(
    val type: Type,
    val displayActivityInfo: DisplayActivityInfo,
    val hostState: MutableMap<String, Boolean>
) {
    enum class Type {
        Confirm, DeleteAll, AddAll
    }
}

@Composable
internal fun hostDialog(
    activity: Activity,
    fetch: suspend (HostDialogState) -> PreferredAppSettingsViewModel.HostStateResult,
    onClose: OnClose<HostDialogCloseState?> = {},
) =
    dialogHelper<HostDialogState, PreferredAppSettingsViewModel.HostStateResult, HostDialogCloseState>(
        fetch = fetch,
        onClose = onClose,
        awaitFetchBeforeOpen = true,
        notifyCloseNoState = false
    ) { state, close ->
        val (displayActivityInfo, hasAppHosts, hostState) = state!!

        val context = LocalContext.current
        DialogColumn {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(defaultRoundedCornerShape)
                    .clickable {
                        activity.startPackageInfoActivity(displayActivityInfo)
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(5.dp))
                Image(
                    bitmap = displayActivityInfo.getIcon(context),
                    contentDescription = displayActivityInfo.label,
                    modifier = Modifier.size(42.dp)
                )

                Text(
                    text = displayActivityInfo.label,
                    fontFamily = HkGroteskFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            DialogSpacer()

            DialogContent(
                items = hostState,
                key = { it },
                bottomRow = {
                    Row(modifier = Modifier.height(40.dp)) {
                        OutlinedButton(
                            contentPadding = PaddingValues(horizontal = 18.dp),
                            onClick = {
                                close(
                                    HostDialogCloseState(
                                        HostDialogCloseState.Type.DeleteAll,
                                        displayActivityInfo,
                                        hostState
                                    )
                                )
                            }
                        ) {
                            Text(text = stringResource(id = R.string.remove_all))
                        }

                        if (hasAppHosts) {
                            Spacer(modifier = Modifier.width(5.dp))

                            OutlinedButton(
                                contentPadding = PaddingValues(horizontal = 18.dp),
                                onClick = {
                                    close(
                                        HostDialogCloseState(
                                            HostDialogCloseState.Type.AddAll,
                                            displayActivityInfo,
                                            hostState
                                        )
                                    )
                                }
                            ) {
                                Text(text = stringResource(id = R.string.add_all))
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            close(
                                HostDialogCloseState(
                                    HostDialogCloseState.Type.Confirm,
                                    displayActivityInfo,
                                    hostState
                                )
                            )
                        }) {
                            Text(text = stringResource(id = R.string.confirm))
                        }
                    }
                },
                content = { host, enabled ->
                    val enabledState = remember { mutableStateOf(enabled) }
                    val update: (Boolean) -> Unit = remember { { hostState[host] = it } }

                    ClickableRow(
                        paddingVertical = 5.dp,
                        verticalAlignment = Alignment.CenterVertically,
                        onClick = enabledState.updateState(update)
                    ) {
                        Checkbox(
                            checked = enabledState.value,
                            onCheckedChange = enabledState.updateStateFromResult(update)
                        )

                        Spacer(modifier = Modifier.width(5.dp))
                        Text(text = host)
                    }

                    Spacer(modifier = Modifier.height(5.dp))
                }
            )
        }
    }
