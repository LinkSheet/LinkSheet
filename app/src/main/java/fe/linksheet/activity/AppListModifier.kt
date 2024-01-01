package fe.linksheet.activity

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fe.linksheet.resolver.DisplayActivityInfo

typealias AppListModifier = @Composable (index: Int, info: DisplayActivityInfo) -> Modifier
