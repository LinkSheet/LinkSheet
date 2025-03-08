package fe.linksheet.composable.component.page.twoline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import fe.android.compose.text.TextContent
import fe.android.preference.helper.Preference
import fe.android.preference.helper.compose.StatePreference
import fe.composekit.component.list.column.group.ListItemData
import fe.composekit.component.list.column.group.RememberGroupDslMarker
import fe.composekit.component.list.column.group.RememberGroupScope
import fe.composekit.preference.ViewModelStatePreference


@Stable
class SwitchPreferenceItem(
    val preference: StatePreference<Boolean>,
    headlineContent: TextContent,
    supportingContent: TextContent,
) : ListItemData<Any?>(headlineContent = headlineContent, subtitleContent = supportingContent)

@Stable
class SwitchPreferenceItemNew(
    val preference: ViewModelStatePreference<Boolean, Boolean, Preference.Default<Boolean>>,
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
    fn: (T) -> List<SwitchPreferenceItemNew>,
): List<SwitchPreferenceItemNew> {
    return remember(key1 = key1) { fn(key1) }
}
