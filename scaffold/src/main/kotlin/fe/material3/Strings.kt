package fe.material3

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext

@Composable
@ReadOnlyComposable
 fun getString(stringId: Int): String {
    val resources = LocalContext.current.resources
    return resources.getString(stringId)
}
