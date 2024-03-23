package fe.linksheet.extension.compose

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.ReadOnlyComposable

@ReadOnlyComposable
@Composable
@Deprecated(message = "Use LocalActivity.current instead", replaceWith = ReplaceWith("LocalActivity.current"))
fun <T> CompositionLocal<T>.currentActivity() = current as Activity
