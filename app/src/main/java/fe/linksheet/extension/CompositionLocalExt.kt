package fe.linksheet.extension

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.currentComposer

@ReadOnlyComposable
@Composable
fun <T> CompositionLocal<T>.currentActivity() = current as Activity