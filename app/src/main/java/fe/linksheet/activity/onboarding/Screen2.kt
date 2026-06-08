package fe.linksheet.activity.onboarding

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import app.linksheet.compose.theme.HkGroteskFontFamily
import app.linksheet.feature.shizuku.service.ShizukuStatus
import app.linksheet.feature.shizuku.usecase.ShizukuStatusUseCase
import fe.composekit.lifecycle.collectRefreshableAsStateWithLifecycle
import fe.linksheet.R


internal val ShizukuStatus.stringRes: Int
    get() = when {
        !installed -> R.string.onboarding_2_install_shizuku
        !running -> R.string.onboarding_2_start_shizuku
        !permission -> R.string.onboarding_2_request_shizuku_permission
        else -> R.string.onboarding_2_shizuku_enabled
    }


@Composable
fun Screen2(
    useCase: ShizukuStatusUseCase,
    padding: PaddingValues,
    onNextClick: () -> Unit
) {
    val activity = LocalActivity.current!!

    val status by useCase.status.collectRefreshableAsStateWithLifecycle(
        minActiveState = Lifecycle.State.RESUMED
    )

    val scope = rememberCoroutineScope()

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
                                when {
                                    !status.installed -> useCase.openShizukuWeb(activity)
                                    !status.running -> useCase.startManager(activity)
                                    !status.permission -> useCase.requestPermission()
                                }
                            }
                        ) {
                            Text(text = stringResource(id = status.stringRes))
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
