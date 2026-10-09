package fe.linksheet.debug.module.debug

import androidx.lifecycle.LifecycleCoroutineScope
import app.linksheet.compose.debug.DebugPreferenceProvider
import fe.composekit.preference.asFlow
import fe.linksheet.debug.module.preference.DebugPreferenceRepository
import fe.linksheet.debug.module.preference.DebugPreferences
import fe.linksheet.material3.M3Log
import kotlinx.coroutines.launch

class RealDebugPreferenceProvider(
    repository: DebugPreferenceRepository,
    lifecycleScope: LifecycleCoroutineScope
) : DebugPreferenceProvider {

    override val drawBorders = repository.asFlow(DebugPreferences.drawBorders)
    override val bottomSheetLog = repository.asFlow(DebugPreferences.bottomSheetLog)
    override val m3Log = repository.asFlow(DebugPreferences.m3Log)

    init {
        M3Log.setEnabled(m3Log.value)
        lifecycleScope.launch {
            m3Log.collect {
                M3Log.setEnabled(it)
            }
        }
    }
}
