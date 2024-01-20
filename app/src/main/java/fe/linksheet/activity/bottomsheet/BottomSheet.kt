package fe.linksheet.activity.bottomsheet

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.lifecycleScope
import fe.linksheet.R
import fe.linksheet.activity.BottomSheetActivity
import fe.linksheet.extension.android.initPadding
import fe.linksheet.extension.android.showToast
import fe.linksheet.extension.android.startPackageInfoActivity
import fe.linksheet.extension.compose.setContentWithKoin
import fe.linksheet.interconnect.LinkSheetConnector
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import fe.linksheet.resolver.BottomSheetResult
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.util.PrivateBrowsingBrowser
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async

abstract class BottomSheet(
    private val activity: BottomSheetActivity,
    protected val viewModel: BottomSheetViewModel,
    protected val initPadding: Boolean = false
) {
    fun launch() {
        if (initPadding) {
            activity.initPadding()
        }

        val deferred = resolveAsync(viewModel)
        if (viewModel.showLoadingBottomSheet()) {
            activity.setContentWithKoin {
                LaunchedEffect(viewModel.resolveResult) {
                    (viewModel.resolveResult as? BottomSheetResult.BottomSheetSuccessResult)?.let {
                        showResolveToasts(it)
                    }
                }

                ShowSheet(viewModel)
            }
        } else {
            deferred.invokeOnCompletion {
                activity.setContentWithKoin { ShowSheet(viewModel) }
            }
        }
    }

    private fun showResolveToasts(result: BottomSheetResult.BottomSheetSuccessResult, uiThread: Boolean = false) {
        viewModel.getResolveToastTexts(result.resolveModuleStatus).forEach {
            activity.showToast(it, uiThread = uiThread)
        }
    }

    private fun resolveAsync(viewModel: BottomSheetViewModel): Deferred<Unit> {
        return activity.lifecycleScope.async {
            val completed = viewModel.resolveAsync(intent, activity.referrer).await()

            if (completed is BottomSheetResult.BottomSheetSuccessResult && completed.hasAutoLaunchApp) {
                showResolveToasts(completed, uiThread = true)

                if (viewModel.openingWithAppToast.value) {
                    activity.showToast(getString(R.string.opening_with_app, completed.app.label), uiThread = true)
                }

                launchApp(
                    completed,
                    completed.app,
                    always = completed.isRegularPreferredApp,
                    persist = false,
                )
            }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    fun launchApp(
        result: BottomSheetResult.BottomSheetSuccessResult,
        info: DisplayActivityInfo,
        always: Boolean = false,
        privateBrowsingBrowser: PrivateBrowsingBrowser? = null,
        persist: Boolean = true,
    ) {
        val deferred = viewModel.launchAppAsync(
            info, result.intent, always, privateBrowsingBrowser,
            persist,
        )

        deferred.invokeOnCompletion {
            val showAsReferrer = viewModel.showAsReferrer.value
            val intent = deferred.getCompleted()

            intent.putExtra(
                LinkSheetConnector.EXTRA_REFERRER,
                if (showAsReferrer) Uri.parse("android-app://${packageName}") else activity.referrer,
            )

            if (!showAsReferrer) {
                intent.putExtra(Intent.EXTRA_REFERRER, activity.referrer)
            }

            startActivity(intent)

            finish()
        }
    }

    @Composable
    abstract fun ShowSheet(bottomSheetViewModel: BottomSheetViewModel)

    protected val intent: Intent = activity.intent
    protected val resources: Resources = activity.resources
    protected val packageName: String = activity.packageName

    protected fun showToast(@StringRes textId: Int, uiThread: Boolean = false) {
        activity.showToast(textId = textId, uiThread = uiThread)
    }

    protected fun startActivity(intent: Intent) {
        activity.startActivity(intent)
    }

    protected fun finish() {
        activity.finish()
    }

    protected fun startPackageInfoActivity(info: DisplayActivityInfo) {
        activity.startPackageInfoActivity(info)
    }

    protected fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String {
        return activity.getString(resId, *formatArgs)
    }
}
