package fe.linksheet.module.viewmodel

import android.app.Application
import fe.android.preference.helper.PreferenceRepository
import fe.linksheet.module.viewmodel.base.BaseViewModel

class ShizukuViewModel(
    val context: Application,
    val preferenceRepository: PreferenceRepository
) : BaseViewModel(preferenceRepository) {

}