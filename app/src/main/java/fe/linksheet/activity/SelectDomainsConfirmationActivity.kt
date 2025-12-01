package fe.linksheet.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.content.IntentCompat
import androidx.core.os.bundleOf
import fe.android.compose.dialog.helper.dialogHelper
import fe.linksheet.BuildConfig
import fe.linksheet.R
import fe.linksheet.composable.ui.AppTheme
import fe.linksheet.composable.util.*
import fe.linksheet.extension.android.getApplicationInfoCompat
import fe.linksheet.interconnect.IDomainSelectionResultCallback
import fe.linksheet.interconnect.StringParceledListSlice
import fe.linksheet.module.database.entity.AppSelectionHistory
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.repository.AppSelectionHistoryRepository
import fe.linksheet.module.repository.PreferredAppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SelectDomainsConfirmationActivity : BaseComponentActivity() {
    companion object {
        const val ACTION_CONFIRM = "${BuildConfig.APPLICATION_ID}.action.CONFIRM_SELECTION"
        const val EXTRA_CALLING_COMPONENT = "calling_component"
        const val EXTRA_CALLING_PACKAGE = "calling_package"
        const val EXTRA_DOMAINS = "domains"
        const val EXTRA_CALLBACK = "callback"

        fun start(
            context: Context,
            callingPackage: String,
            callingComponent: ComponentName,
            domains: StringParceledListSlice,
            callback: IDomainSelectionResultCallback? = null,
        ) {
            val intent = Intent(context, SelectDomainsConfirmationActivity::class.java)

            intent.action = ACTION_CONFIRM
            intent.putExtra(EXTRA_CALLING_PACKAGE, callingPackage)
            intent.putExtra(EXTRA_DOMAINS, domains)
            intent.putExtra(EXTRA_CALLING_COMPONENT, callingComponent)
            intent.putExtra(EXTRA_CALLBACK, bundleOf(EXTRA_CALLBACK to callback?.asBinder()))
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(intent)
        }
    }

    private val preferredAppRepository: PreferredAppRepository by inject()
    private val appSelectionHistoryRepository: AppSelectionHistoryRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent?.action != ACTION_CONFIRM) return

        val callingComponent = IntentCompat.getParcelableExtra(
            intent,
            EXTRA_CALLING_COMPONENT,
            ComponentName::class.java,
        )
        val callingPackage = intent.getStringExtra(EXTRA_CALLING_PACKAGE)
        val domains = IntentCompat.getParcelableExtra(
            intent,
            EXTRA_DOMAINS,
            StringParceledListSlice::class.java
        )?.list
        val callback = intent.getBundleExtra(EXTRA_CALLBACK)
            ?.getBinder(EXTRA_CALLBACK)
            ?.let { IDomainSelectionResultCallback.Stub.asInterface(it) }

        if (callingPackage == null || domains == null || callingComponent == null) {
            finish()
            return
        }

        val appLabel = packageManager.getApplicationInfoCompat(callingPackage, 0)
            ?.loadLabel(packageManager) ?: callingPackage

        setContent(edgeToEdge = true) {
            val scope = rememberCoroutineScope()

            var loading by remember {
                mutableStateOf(false)
            }

            AppTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    AnimatedVisibility(visible = loading) {
                        CircularProgressIndicator()
                    }

                    // TODO: Allow for user to deselect specific requested domains.
                    val dialog = dialogHelper(
                        state = domains,
                        onClose = { confirmed ->
                            scope.launch(Dispatchers.IO) {
                                if (confirmed == true) {
                                    loading = true

                                    val preferred = mutableListOf<PreferredApp>()
                                    val history = mutableListOf<AppSelectionHistory>()

                                    domains.forEach { domain ->
                                        preferred.add(
                                            PreferredApp.new(
                                                host = domain,
                                                pkg = callingPackage,
                                                cmp = callingComponent,
                                                always = true,
                                            )
                                        )

                                        history.add(
                                            AppSelectionHistory(
                                                host = domain,
                                                packageName = callingPackage,
                                                lastUsed = System.currentTimeMillis(),
                                            )
                                        )
                                    }

                                    preferredAppRepository.insert(preferred)
                                    appSelectionHistoryRepository.insert(history)

                                    loading = false
                                }

                                if (confirmed == true) {
                                    callback?.onDomainSelectionConfirmed()
                                } else {
                                    callback?.onDomainSelectionCancelled()
                                }

                                finish()
                            }
                        },
                        dynamicHeight = true,
                        notifyCloseNoState = true,
                    ) { state, close ->
                        DialogColumn {
                            HeadlineText(headlineId = R.string.domain_selection_confirmation_title)
                            SubtitleText(
                                subtitle = stringResource(
                                    id = R.string.domain_selection_confirmation_subtitle,
                                    appLabel
                                )
                            )
                            DialogSpacer()
                            DialogContent(
                                items = state,
                                key = { it },
                                bottomRow = {
                                    TextButton(
                                        onClick = {
                                            close(false)
                                        },
                                    ) {
                                        Text(text = stringResource(id = R.string.no))
                                    }

                                    TextButton(
                                        onClick = {
                                            close(true)
                                        },
                                        colors = ButtonDefaults.textButtonColors(
                                            contentColor = MaterialTheme.colorScheme.error,
                                        ),
                                    ) {
                                        Text(text = stringResource(id = R.string.yes))
                                    }
                                },
                                content = {
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Text(text = it)
                                    }
                                },
                            )
                        }
                    }

                    LaunchedEffect(null) {
                        dialog.open()
                    }
                }
            }
        }
    }

    @Composable
    private fun Dialog() {

    }
}
