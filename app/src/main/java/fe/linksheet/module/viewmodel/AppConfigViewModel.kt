package fe.linksheet.module.viewmodel

import android.app.Application
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.viewmodel.base.BaseViewModel

class AppConfigViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {


}
