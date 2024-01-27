package fe.linksheet.activity.onboarding._new

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.util.TableInfo
import fe.linksheet.R
import fe.linksheet.composable.settings.apps.link.AppsWhichCanOpenLinksSettingsRoute
import fe.linksheet.ui.HkGroteskFontFamily
import fe.linksheet.util.AndroidVersion

@Composable
fun Screen4(onBackPressed: () -> Unit, onNextClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().navigationBarsPadding()) {
        Column(modifier = Modifier.weight(1f)) {
            if (AndroidVersion.AT_LEAST_API_31_S) {
                AppsWhichCanOpenLinksSettingsRoute(onBackPressed = onBackPressed)
            }
        }

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
                    text = stringResource(id = R.string.onboarding_4_card_title),
                    overflow = TextOverflow.Visible,
                    fontSize = 20.sp,
                    fontFamily = HkGroteskFontFamily,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(5.dp))
                Text(text = stringResource(id = R.string.onboarding_4_card_explainer))
                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    modifier = Modifier.height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    onClick = onNextClick
                ) {
                    Text(text = stringResource(id = R.string.onboarding_4_button))
                }

            }
        }
    }
}
