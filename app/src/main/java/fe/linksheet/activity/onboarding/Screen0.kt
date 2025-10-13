package fe.linksheet.activity.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.linksheet.R
import app.linksheet.compose.theme.HkGroteskFontFamily

@Composable
fun Screen0(padding: PaddingValues, onNextClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(), contentPadding = PaddingValues(horizontal = 30.dp)
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.onboarding_0_subtitle),
                    overflow = TextOverflow.Visible,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontFamily = HkGroteskFontFamily,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Box(modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .navigationBarsPadding()) {
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
                        text = stringResource(id = R.string.onboarding_setup),
                        overflow = TextOverflow.Visible,
                        fontSize = 20.sp,
                        fontFamily = HkGroteskFontFamily,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(text = stringResource(id = R.string.start_setup_explainer))

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        modifier = Modifier.height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        onClick = onNextClick
                    ) {
                        Text(text = stringResource(id = R.string.start_setup))
                    }
                }
            }
        }
    }
}
