package fe.linksheet.extension.compose

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
fun ComponentActivity.setContentWithKoin(content: @Composable () -> Unit) {
    setContent { KoinAndroidContext(content) }
}
