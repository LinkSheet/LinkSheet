package fe.linksheet.module.viewmodel.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences

abstract class BaseViewModel(
    preferenceRepository: AppPreferenceRepository,
//  protected val stateCache: StateCache = StateCache()
) : ViewModel() {
    val alwaysShowPackageName = preferenceRepository.asViewModelState(AppPreferences.alwaysShowPackageName)

//    @JvmName
//    public final fun asState(
//        preference: Preference.Default<Boolean>
//    ): MutablePreferenceState<Boolean, Boolean, Preference.Default<Boolean>>
//
//    fe.android.preference.helper.compose.StatePreferenceRepository




    init {
//        Log.d("ViewModel", getTag())
    }

    // Unused for now
//    @Composable
//    fun bindStateLifetime(navController: NavController): () -> Unit {
//        val onBack: () -> Unit = {
//            stateCache.close()
//            navController.popBackStack()
//        }
//
//        BackHandler(true, onBack)
//
//        val lifecycleState = LocalLifecycleOwner.current.lifecycle.observeAsState()
//        LaunchedEffect(lifecycleState.state) {
//            if (lifecycleState.state == Lifecycle.Event.ON_PAUSE) stateCache.close()
//        }
//
//        return onBack
//    }
}
