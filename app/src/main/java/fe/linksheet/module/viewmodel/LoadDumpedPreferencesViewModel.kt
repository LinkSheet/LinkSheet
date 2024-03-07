package fe.linksheet.module.viewmodel

import android.app.Application
import android.util.Log
import fe.linksheet.module.preference.AppPreferenceRepository
import fe.linksheet.module.preference.AppPreferences
import fe.linksheet.module.preference.preferenceRepositoryModule

import fe.linksheet.module.viewmodel.base.BaseViewModel

class LoadDumpedPreferencesViewModel(
    val context: Application,
    val preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {

    fun importText(text: String) {
        val lines = text.lines().mapNotNull { line ->
            val splitted = line.split("=")
            if (splitted.size != 2) return@mapNotNull null

            splitted[0] to splitted[1]
        }

        preferenceRepository.edit {
            lines.forEach { (key, value) ->
                val preference = AppPreferences.all[key]
                if (preference != null) {
                    try {
                        preferenceRepository.setStringValueToPreference(
                            preference,
                            value,
                        )
                    } catch (e: Exception) {
                        Log.d("Import", "Failed to import $key")
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}
