package fe.linksheet.extension.compose

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.ReadOnlyComposable

@ReadOnlyComposable
@Composable
fun <T> CompositionLocal<T>.currentActivity() = current as Activity