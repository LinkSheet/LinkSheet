package fe.linksheet.activity.bottomsheet

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasomaniac.openwith.data.LinkSheetDatabase
import com.tasomaniac.openwith.data.PreferredApp
import com.tasomaniac.openwith.resolver.IntentResolverResult
import com.tasomaniac.openwith.resolver.ResolveIntents
import fe.linksheet.module.preference.PreferenceRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BottomSheetViewModel : ViewModel(),
    KoinComponent {
    private val database by inject<LinkSheetDatabase>()
    private val preferenceRepository by inject<PreferenceRepository>()

    var result by mutableStateOf<IntentResolverResult?>(null)
    var enableCopyButton by mutableStateOf(preferenceRepository.getBoolean(PreferenceRepository.enableCopyButton) ?: false)
    var singleTap by mutableStateOf(preferenceRepository.getBoolean(PreferenceRepository.singleTap) ?: false)
    var enableSendButton by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.enableSendButton) ?: false
    )
    fun resolveAsync(context: Context, intent: Intent): Deferred<IntentResolverResult?> {
        return viewModelScope.async(Dispatchers.IO) {
            result = ResolveIntents.resolve(context, intent)

            result
        }
    }

    fun persistSelectedIntent(intent: Intent, always: Boolean) {
        Log.d("PersistingSelectedIntent", "Component: ${intent.component}")
        val component = intent.component ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val app = PreferredApp(
                host = intent.data!!.host!!,
                component = component.flattenToString(),
                alwaysPreferred = always
            )

            Log.d("PersistingSelectedIntent", "Inserting $app")
            database.preferredAppDao().insert(app)
        }
    }
}