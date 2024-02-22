package fe.linksheet.activity

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.pm.CrossProfileApps
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.content.getSystemService
import fe.android.preference.helper.compose.getBooleanState
import fe.linksheet.activity.bottomsheet.DevBottomSheet
import fe.linksheet.activity.bottomsheet.LegacyBottomSheet
import fe.linksheet.activity.bottomsheet.NewBottomSheet
import fe.linksheet.module.log.hasher.ComponentNameDumpable
import fe.linksheet.module.preference.AppPreferences
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import fe.linksheet.module.viewmodel.DevSettingsViewModel
import fe.linksheet.module.viewmodel.FeatureFlagViewModel
import fe.linksheet.util.AndroidVersion
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent

class BottomSheetActivity : ComponentActivity(), KoinComponent {
    private val bottomSheetViewModel by viewModel<BottomSheetViewModel>()

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        Log.d("CrossProfile", "test")
//        if (AndroidVersion.AT_LEAST_API_28_P) {
//            val crossProfileApps = getSystemService<CrossProfileApps>()!!
//            Log.d("CrossProfile", "$crossProfileApps ${crossProfileApps.targetUserProfiles.size}")
//
//            val targetUserProfile = crossProfileApps.targetUserProfiles.firstOrNull()
//            if (targetUserProfile != null) {
//                Log.d("CrossProfile", "$targetUserProfile")
//
//                val comp = ComponentName(componentName.packageName, MainActivity::class.java.name)
//
////                crossProfileApps.startMainActivity(comp, targetUserProfile)
////                crossProfileApps.startActivity(comp, targetUserProfile, null)
//
////                crossProfileApps.canInteractAcrossProfiles()
////                if (AndroidVersion.AT_LEAST_API_30_R) {
////                    startActivity(crossProfileApps.createRequestInteractAcrossProfilesIntent())
////                }
//            }
//        }


//        val bottomSheet = if (devSettingsViewModel.devBottomSheetExperiment()) {
//            DevBottomSheet(this, bottomSheetViewModel)
//        } else {
//            LegacyBottomSheet(this, bottomSheetViewModel)
//        }

        DevBottomSheet(this, bottomSheetViewModel).launch()
    }

    override fun onStop() {
        super.onStop()
        finish()
    }
}
