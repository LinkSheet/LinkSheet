package fe.linksheet.experiment.improved.resolver.material3

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

@Composable
@ReadOnlyComposable
 fun getString(stringId: Int): String {
    LocalConfiguration.current
    val resources = LocalContext.current.resources
    return resources.getString(stringId)
}
