package fe.linksheet.activity.bottomsheet

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasomaniac.openwith.data.LinkSheetDatabase
import com.tasomaniac.openwith.data.PreferredApp
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import com.tasomaniac.openwith.resolver.IntentResolverResult
import com.tasomaniac.openwith.resolver.ResolveIntents
import fe.linksheet.activity.MainActivity
import fe.linksheet.data.AppSelectionHistory
import fe.linksheet.data.WhitelistedBrowser
import fe.linksheet.extension.startActivityWithConfirmation
import fe.linksheet.module.preference.PreferenceRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class BottomSheetViewModel : ViewModel(),
    KoinComponent {
    private val database by inject<LinkSheetDatabase>()
    private val preferenceRepository by inject<PreferenceRepository>()

    var result by mutableStateOf<IntentResolverResult?>(null)
    var enableCopyButton by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.enableCopyButton) ?: false
    )

    var hideAfterCopying by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.hideAfterCopying) ?: false
    )

    var singleTap by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.singleTap) ?: false
    )

    var enableSendButton by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.enableSendButton) ?: false
    )

    var alwaysShowPackageName by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.alwaysShowPackageName) ?: false
    )

    var disableToasts by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.disableToasts) ?: false
    )

    var gridLayout by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.gridLayout) ?: false
    )

    fun resolveAsync(context: Context, intent: Intent): Deferred<IntentResolverResult?> {
        return viewModelScope.async(Dispatchers.IO) {
            result = ResolveIntents.resolve(context, intent, this@BottomSheetViewModel)

            result
        }
    }

    fun startMainActivity(context: Context): Boolean {
        return context.startActivityWithConfirmation(Intent(context, MainActivity::class.java))
    }

    fun startPackageInfoActivity(context: Context, info: DisplayActivityInfo): Boolean {
        return context.startActivityWithConfirmation(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            this.data = Uri.parse("package:${info.packageName}")
        })
    }

    suspend fun persistSelectedIntentAsync(intent: Intent, always: Boolean) {
        Log.d("PersistingSelectedIntent", "Component: ${intent.component}")
        return withContext(Dispatchers.IO) {
            intent.component?.let { component ->
                val host = intent.data!!.host!!.lowercase(Locale.getDefault())
                val app = PreferredApp(
                    host = host,
                    packageName = component.packageName,
                    component = component.flattenToString(),
                    alwaysPreferred = always
                )

                Log.d("PersistingSelectedIntent", "Inserting $app")
                database.preferredAppDao().insert(app)

                val historyEntry = AppSelectionHistory(
                    host = host,
                    packageName = component.packageName,
                    lastUsed = System.currentTimeMillis()
                )

                database.appSelectionHistoryDao().insert(historyEntry)
                Log.d("PersistingSelectedIntent", "Inserting $historyEntry")
            }
        }
    }

    suspend fun getWhiteListedBrowsers(): List<WhitelistedBrowser> {
        return withContext(Dispatchers.IO) {
            database.whitelistedBrowsersDao().getWhitelistedBrowsers()
        }
    }
}