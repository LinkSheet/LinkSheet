package fe.linksheet.activity.onboarding

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import app.linksheet.compose.theme.HkGroteskFontFamily
import dev.zwander.shared.ShizukuUtil
import fe.linksheet.R
import fe.linksheet.extension.compose.ObserveStateChange
import fe.linksheet.extension.compose.focusGainedEvents
import fe.linksheet.module.shizuku.ShizukuStatus
import fe.linksheet.util.ShizukuDownload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val statusMap = mapOf(
    ShizukuStatus.Enabled to R.string.onboarding_2_shizuku_enabled,
    ShizukuStatus.NotRunning to R.string.onboarding_2_start_shizuku,
    ShizukuStatus.NoPermission to R.string.onboarding_2_request_shizuku_permission,
    ShizukuStatus.NotInstalled to R.string.onboarding_2_install_shizuku,
)

@Composable
fun Screen2(padding: PaddingValues, onNextClick: () -> Unit) {
    val activity = LocalActivity.current!!
    val uriHandler = LocalUriHandler.current

    var shizukuInstalled by remember { mutableStateOf(ShizukuUtil.isShizukuInstalled(activity)) }
    var shizukuRunning by remember { mutableStateOf(ShizukuUtil.isShizukuRunning()) }

    val shizukuPermission by ShizukuUtil.rememberHasShizukuPermissionAsState()

    var status = ShizukuStatus.findStatus(shizukuInstalled, shizukuRunning, shizukuPermission)
    val statusStringId = statusMap[status]!!

    val scope = rememberCoroutineScope()

    LocalLifecycleOwner.current.lifecycle.ObserveStateChange(observeEvents = focusGainedEvents) {
        shizukuInstalled = ShizukuUtil.isShizukuInstalled(activity)
        shizukuRunning = ShizukuUtil.isShizukuRunning()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 30.dp)
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.onboarding_2_subtitle),
                    overflow = TextOverflow.Visible,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontFamily = HkGroteskFontFamily,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(
                    topStart = 25.dp,
                    bottomStart = 0.dp,
                    topEnd = 25.dp,
                    bottomEnd = 0.dp
                )
            ) {
                Column(modifier = Modifier.padding(all = 25.dp)) {
                    Text(
                        text = stringResource(id = R.string.onboarding_2_card_title),
                        overflow = TextOverflow.Visible,
                        fontSize = 20.sp,
                        fontFamily = HkGroteskFontFamily,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = stringResource(id = R.string.onboarding_2_card_explainer))
                    Spacer(modifier = Modifier.height(10.dp))


                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Button(
                            modifier = Modifier.height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            onClick = {
                                when (status) {
                                    ShizukuStatus.NoPermission -> scope.launch(Dispatchers.IO) {
                                        if (ShizukuUtil.requestPermission()) {
                                            status = ShizukuStatus.findStatus(shizukuInstalled, shizukuRunning, shizukuPermission)
                                        }
                                    }

                                    ShizukuStatus.NotInstalled -> uriHandler.openUri(ShizukuDownload)
                                    ShizukuStatus.NotRunning -> ShizukuUtil.startManager(activity)
                                    ShizukuStatus.Enabled -> onNextClick()
                                }

                            }
                        ) {
                            Text(text = stringResource(id = statusStringId))
                        }

                        TextButton(onClick = onNextClick) {
                            Text(text = stringResource(id = R.string.onboarding_2_skip_button))
                        }
                    }
                }
            }
        }
    }
}
