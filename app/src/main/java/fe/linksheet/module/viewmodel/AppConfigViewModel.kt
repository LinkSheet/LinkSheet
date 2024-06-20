package fe.linksheet.module.viewmodel

import android.app.Application
import com.google.gson.Gson
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.util.AppInfo

class AppConfigViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {


}
