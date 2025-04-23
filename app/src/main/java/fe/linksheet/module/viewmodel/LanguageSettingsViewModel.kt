package fe.linksheet.module.viewmodel

import fe.linksheet.module.language.AppLocaleService
import fe.linksheet.module.language.LocaleItem
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.viewmodel.base.BaseViewModel

class LanguageSettingsViewModel(
    val localeService: AppLocaleService,
    val preferenceRepository: AppPreferenceRepository,
) : BaseViewModel(preferenceRepository) {

    val localesFlow = localeService.localesFlow
    val appLocaleItemFlow = localeService.appLocaleItemFlow
    val deviceLocaleFlow = localeService.deviceLocaleFlow

    fun update(it: LocaleItem) {
        localeService.update(it)
    }
}
