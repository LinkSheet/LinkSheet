package fe.linksheet.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import fe.android.preference.helper.compose.getBooleanState
import fe.linksheet.activity.bottomsheet.DevBottomSheet
import fe.linksheet.activity.bottomsheet.LegacyBottomSheet
import fe.linksheet.activity.bottomsheet.NewBottomSheet
import fe.linksheet.module.preference.AppPreferences
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import fe.linksheet.module.viewmodel.DevSettingsViewModel
import fe.linksheet.module.viewmodel.FeatureFlagViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent

class BottomSheetActivity : ComponentActivity(), KoinComponent {
    private val bottomSheetViewModel by viewModel<BottomSheetViewModel>()
    private val featureFlagViewModel by viewModel<FeatureFlagViewModel>()
    private val devSettingsViewModel by viewModel<DevSettingsViewModel>()

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bottomSheet = if (devSettingsViewModel.devBottomSheetExperiment()) {
            DevBottomSheet(this, bottomSheetViewModel)
        } else {
            LegacyBottomSheet(this, bottomSheetViewModel)
        }

        bottomSheet.launch()
    }
}
