@file:OptIn(ExperimentalTime::class)

package fe.linksheet.module.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class ExportSettingsViewModel(
    val context: Application,
    val gson: Gson,
    clock: Clock,
) : ViewModel() {
}
