package fe.linksheet.experiment.ui.overhaul.composable.component.page.twoline

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import fe.android.preference.helper.compose.StatePreference
import fe.linksheet.experiment.ui.overhaul.composable.component.page.RememberGroupDslMarker
import fe.linksheet.experiment.ui.overhaul.composable.component.page.RememberGroupScope
import fe.linksheet.experiment.ui.overhaul.composable.component.page.TwoLineGroupValueProvider

@Stable
class TwoLinePreference(
    val preference: StatePreference<Boolean>,
    headlineId: Int,
    subtitleId: Int,
) : TwoLineGroupValueProvider(headlineId, subtitleId)

@RememberGroupDslMarker
class TwoLinePreferenceScope : RememberGroupScope<Int, TwoLinePreference>() {
    fun add(preference: StatePreference<Boolean>, @StringRes headlineId: Int, @StringRes subtitleId: Int) {
        add(TwoLinePreference(preference, headlineId, subtitleId))
    }
}

@Composable
fun <T : Any?> rememberTwoLinePreferenceGroup(
    key1: T,
    fn: TwoLinePreferenceScope.(T) -> Unit,
): List<TwoLinePreference> {
    return remember(key1 = key1) {
        val scope = TwoLinePreferenceScope()
        scope.fn(key1)
        scope.providers
    }
}
