package fe.linksheet.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.content.IntentCompat
import fe.android.compose.dialog.helper.dialogHelper
import fe.linksheet.BuildConfig
import fe.linksheet.R
import fe.linksheet.composable.util.DialogColumn
import fe.linksheet.composable.util.DialogContent
import fe.linksheet.composable.util.DialogSpacer
import fe.linksheet.composable.util.HeadlineText
import fe.linksheet.composable.util.SubtitleText
import fe.linksheet.extension.android.getApplicationInfoCompat
import fe.linksheet.extension.android.initPadding
import fe.linksheet.interconnect.StringParceledListSlice
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.ui.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SelectDomainsConfirmationActivity : ComponentActivity() {
    companion object {
        const val ACTION_CONFIRM = "${BuildConfig.APPLICATION_ID}.action.CONFIRM_SELECTION"
        const val EXTRA_CALLING_COMPONENT = "calling_component"
        const val EXTRA_CALLING_PACKAGE = "calling_package"
        const val EXTRA_DOMAINS = "domains"

        fun start(
            context: Context,
            callingPackage: String,
            callingComponent: ComponentName,
            domains: StringParceledListSlice
        ) {
            val intent = Intent(context, SelectDomainsConfirmationActivity::class.java)

            intent.action = ACTION_CONFIRM
            intent.putExtra(EXTRA_CALLING_PACKAGE, callingPackage)
            intent.putExtra(EXTRA_DOMAINS, domains)
            intent.putExtra(EXTRA_CALLING_COMPONENT, callingComponent)
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(intent)
        }
    }

    private val repository: PreferredAppRepository by inject()

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

        if (callingPackage == null || domains == null || callingComponent == null) {
            finish()
            return
        }

        initPadding()

        val appLabel = packageManager.getApplicationInfoCompat(callingPackage, 0)
            ?.loadLabel(packageManager) ?: callingPackage

        setContent {
            val scope = rememberCoroutineScope()

            AppTheme {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    val dialog = dialogHelper(
                        state = domains,
                        onClose = { confirmed ->
                            scope.launch(Dispatchers.IO) {
                                if (confirmed == true) {
                                    repository.insert(domains.map {
                                        PreferredApp(
                                            host = it,
                                            packageName = callingPackage,
                                            component = callingComponent.flattenToString(),
                                            alwaysPreferred = true,
                                        )
                                    })
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
}
