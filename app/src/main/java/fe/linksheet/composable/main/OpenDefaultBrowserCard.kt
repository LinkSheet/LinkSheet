package fe.linksheet.composable.main

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.composable.util.ColoredIcon
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.ui.Typography
import fe.linksheet.util.AndroidVersion
import fe.linksheet.util.Results

@Composable
fun OpenDefaultBrowserCard(
    activity: Activity,
    defaultBrowserEnabled: Results<Unit>,
    defaultBrowserChanged: (Results<Unit>) -> Unit,
    viewModel: MainViewModel
) {
    val browserLauncherAndroidQPlus = if (AndroidVersion.AT_LEAST_API_29_Q) {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = {
                defaultBrowserChanged(
                    if (it.resultCode == Activity.RESULT_OK) Results.success()
                    else Results.error()
                )
            }
        )
    } else null

    val shouldUsePrimaryColor = defaultBrowserEnabled.isSuccess || defaultBrowserEnabled.isLoading
    Card(
        colors = CardDefaults.cardColors(containerColor = if (shouldUsePrimaryColor) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.error),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp)
                .clickable {
                    if (defaultBrowserEnabled.isLoading) {
                        return@clickable
                    }

                    if (AndroidVersion.AT_LEAST_API_29_Q && !defaultBrowserEnabled.isSuccess) {
                        val intent = viewModel.getRequestRoleBrowserIntent()
                        browserLauncherAndroidQPlus!!.launch(intent)
                    } else {
                        viewModel.openDefaultBrowserSettings(activity)
                    }
                }, verticalAlignment = Alignment.CenterVertically
        ) {
            val color =
                if (shouldUsePrimaryColor) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onError

            if (defaultBrowserEnabled.isLoading) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = color)
                }
            } else {
                Spacer(modifier = Modifier.width(10.dp))
                ColoredIcon(
                    icon = if (defaultBrowserEnabled.isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                    descriptionId = if (defaultBrowserEnabled.isSuccess) R.string.checkmark else R.string.error,
                    color = color
                )

                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = stringResource(id = if (defaultBrowserEnabled.isSuccess) R.string.browser_status else R.string.set_as_browser),
                        style = Typography.titleLarge,
                        color = color
                    )
                    Text(
                        text = stringResource(id = if (defaultBrowserEnabled.isSuccess) R.string.set_as_browser_done else R.string.set_as_browser_explainer),
                        color = if (defaultBrowserEnabled.isSuccess) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onError
                    )
                }
            }
        }
    }
}
