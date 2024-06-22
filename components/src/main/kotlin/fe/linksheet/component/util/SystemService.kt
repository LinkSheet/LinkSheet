package fe.linksheet.component.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.getSystemService

@Composable
inline fun <reified T : Any> rememberSystemService(context: Context = LocalContext.current): T {
    return remember(context) { context.getSystemService<T>()!! }
}
