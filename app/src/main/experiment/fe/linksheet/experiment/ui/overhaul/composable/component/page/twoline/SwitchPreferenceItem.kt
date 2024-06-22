package fe.linksheet.experiment.ui.overhaul.composable.component.page.twoline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import fe.android.preference.helper.compose.StatePreference
import fe.linksheet.component.page.ListItemData
import fe.linksheet.component.page.RememberGroupDslMarker
import fe.linksheet.component.page.RememberGroupScope
import fe.linksheet.component.util.TextContent

@Stable
class SwitchPreferenceItem(
    val preference: StatePreference<Boolean>,
    headlineContent: TextContent,
    supportingContent: TextContent,
) : ListItemData<Any?>(headlineContent = headlineContent, subtitleContent = supportingContent)

@RememberGroupDslMarker
class TwoLinePreferenceScope : RememberGroupScope<Any, SwitchPreferenceItem>() {
    fun add(
        preference: StatePreference<Boolean>, headlineContent: TextContent,
        supportingContent: TextContent,
    ) {
        add(SwitchPreferenceItem(preference, headlineContent, supportingContent))
    }
}

@Composable
fun <T : Any?> rememberTwoLinePreferenceGroup(
    key1: T,
    fn: (T) -> List<SwitchPreferenceItem>,
): List<SwitchPreferenceItem> {
    return remember(key1 = key1) { fn(key1) }
}
