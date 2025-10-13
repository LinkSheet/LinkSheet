package fe.linksheet.activity.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.composekit.core.AndroidVersion
import fe.linksheet.R
import fe.linksheet.composable.page.settings.apps.verifiedlinkhandlers.VerifiedLinkHandlersRoute
import app.linksheet.compose.theme.HkGroteskFontFamily

@Composable
fun Screen4(onBackClick: () -> Unit, onNextClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().navigationBarsPadding()) {
        Column(modifier = Modifier.weight(1f)) {
            if (AndroidVersion.isAtLeastApi31S()) {
                VerifiedLinkHandlersRoute(onBackPressed = onBackClick, navigateNew = {})
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
