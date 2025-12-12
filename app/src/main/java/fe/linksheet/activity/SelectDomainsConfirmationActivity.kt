package fe.linksheet.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.os.Messenger
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.bundleOf
import app.linksheet.feature.app.core.AppInfo
import app.linksheet.testing.fake.PackageInfoFakes
import app.linksheet.testing.fake.toAppInfo
import fe.composekit.component.PreviewThemeNew
import fe.composekit.extension.getBundleBinder
import fe.composekit.extension.getParcelableExtraCompat
import fe.linksheet.BuildConfig
import fe.linksheet.composable.dialog.AppHostDialogResult
import fe.linksheet.composable.dialog.AppInfoDialogData
import fe.linksheet.composable.dialog.rememberAppInfoDialog
import fe.linksheet.composable.ui.AppTheme
import fe.linksheet.interconnect.IDomainSelectionResultCallback
import fe.linksheet.interconnect.StringParceledListSlice
import fe.linksheet.module.viewmodel.SelectDomainsConfirmationViewModel
import org.koin.androidx.compose.koinViewModel

class SelectDomainsConfirmationActivity : BaseComponentActivity() {
    companion object {
        const val ACTION_CONFIRM = "${BuildConfig.APPLICATION_ID}.action.CONFIRM_SELECTION"
        const val EXTRA_CALLING_COMPONENT = "calling_component"
        const val EXTRA_CALLING_PACKAGE = "calling_package"
        const val EXTRA_DOMAINS = "domains"
        const val EXTRA_CALLBACK = "callback"
        const val EXTRA_MESSENGER = "messenger"
        const val MSG_CHOOSER_FINISHED = 1

        fun start(
            context: Context,
            callingPackage: String,
            callingComponent: ComponentName,
            domains: StringParceledListSlice,
            messenger: Messenger,
            callback: IDomainSelectionResultCallback? = null,
        ) {
            val intent = Intent(context, SelectDomainsConfirmationActivity::class.java)

            intent.action = ACTION_CONFIRM
            intent.putExtra(EXTRA_CALLING_PACKAGE, callingPackage)
            intent.putExtra(EXTRA_DOMAINS, domains)
            intent.putExtra(EXTRA_CALLING_COMPONENT, callingComponent)
            intent.putExtra(EXTRA_CALLBACK, bundleOf(EXTRA_CALLBACK to callback?.asBinder()))
            intent.putExtra(EXTRA_MESSENGER, messenger)
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent?.action != ACTION_CONFIRM) return

        val callingComponent = intent.getParcelableExtraCompat<ComponentName>(EXTRA_CALLING_COMPONENT)
        val callingPackage = intent.getStringExtra(EXTRA_CALLING_PACKAGE)
        val domains = intent.getParcelableExtraCompat<StringParceledListSlice>(EXTRA_DOMAINS)?.list
        val callback = intent.getBundleBinder(EXTRA_CALLBACK) {
            IDomainSelectionResultCallback.Stub.asInterface(it)
        }
        val messenger = intent.getParcelableExtraCompat<Messenger>(EXTRA_MESSENGER)

        fun finishSelection() {
            finish()
            messenger?.send(Message.obtain().apply {
                what = MSG_CHOOSER_FINISHED
            })
        }

        if (callingPackage == null || domains == null || callingComponent == null) {
            finishSelection()
            return
        }

        setContent(edgeToEdge = true) {
            AppTheme {
                AppPageWrapper(
                    packageName = callingPackage,
                    domains = domains,
                    onClose = {
                        callback?.onDomainSelectionConfirmed()
                        finishSelection()
                    },
                    onDismiss = {
                        callback?.onDomainSelectionCancelled()
                        finishSelection()
                    }
                )
            }
        }
    }
}

@Composable
private fun AppPageWrapper(
    packageName: String,
    domains: List<String>,
    onClose: () -> Unit,
    onDismiss: () -> Unit,
    viewModel: SelectDomainsConfirmationViewModel = koinViewModel(),
) {
    val appInfo = remember(packageName) { viewModel.getAppInfoWithHosts(packageName) }
    appInfo?.let { (appInfo, supportedHosts) ->
        AppPageInternal(
            appInfo = appInfo,
            domains = supportedHosts.intersect(domains.toSet()),
            onClose = { (appInfo, hostState) ->
                viewModel.handler.updateHostState(appInfo, hostState)
                onClose()
            },
            onDismiss = onDismiss
        )
    }
}

@Composable
private fun AppPageInternal(
    appInfo: AppInfo,
    domains: Set<String>,
    onClose: (AppHostDialogResult) -> Unit,
    onDismiss: (() -> Unit)? = null,
) {
    val dialogState = rememberAppInfoDialog(
        onClose = onClose,
        onDismiss = onDismiss
    )

    LaunchedEffect(appInfo) {
        dialogState.open(AppInfoDialogData(appInfo, domains.toSet()))
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {

    }
}

@Preview(showBackground = true)
@Composable
private fun AppPageInternalPreview() {
    PreviewThemeNew {
        AppPageInternal(
            appInfo = PackageInfoFakes.MiBrowser.toAppInfo(),
            domains = setOf("google.com", "youtube.com"),
            onClose = { (appInfo, hostStates) ->

            },
            onDismiss = {

            }
        )
    }
}
