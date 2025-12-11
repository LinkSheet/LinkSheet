package fe.linksheet.activity.bottomsheet.content.success.appcontent

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.linksheet.compose.theme.HkGroteskFontFamily
import app.linksheet.feature.app.ActivityAppInfo
import app.linksheet.testing.fake.PackageInfoFakes
import app.linksheet.testing.fake.toActivityAppInfo
import app.linksheet.testing.util.PackageInfoFake
import fe.linksheet.R
import fe.linksheet.composable.component.appinfo.AppInfoIcon

object AppListItemRowDefaults {
    val RowHeight = 60.dp
}

@Composable
fun AppListItemRow(
    modifier: Modifier = Modifier,
    appInfo: ActivityAppInfo,
    preferred: Boolean,
    showPackage: Boolean
) {
    Row(
        modifier = modifier.then(Modifier.height(IntrinsicSize.Min)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AppInfoIcon(appInfo = appInfo)

        Column {
            if (preferred) {
                Text(
                    text = stringResource(id = R.string.open_with_app, appInfo.label),
                    fontFamily = HkGroteskFontFamily,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                Text(text = appInfo.label)
            }

            if (showPackage) {
                Text(
                    text = appInfo.packageName,
                    fontSize = 12.sp,
                    lineHeight = 12.sp
                )
            }
        }
    }
}

private data class PreviewAppListItemState(
    val fake: PackageInfoFake,
    val preferred: Boolean,
    val showPackage: Boolean
)

private class ActivityAppInfoPreviewParameter() : PreviewParameterProvider<PreviewAppListItemState> {
    override val values = sequenceOf(
        PreviewAppListItemState(PackageInfoFakes.Youtube, preferred = false, showPackage = false),
        PreviewAppListItemState(PackageInfoFakes.Youtube, preferred = false, showPackage = true),
        PreviewAppListItemState(PackageInfoFakes.Youtube, preferred = true, showPackage = false),
        PreviewAppListItemState(PackageInfoFakes.Youtube, preferred = true, showPackage = true),
    )
}

@Preview(showBackground = true)
@Composable
private fun AppListItemRowPreview(@PreviewParameter(ActivityAppInfoPreviewParameter::class) state: PreviewAppListItemState) {
    AppListItemRow(
        appInfo = state.fake.toActivityAppInfo(),
        preferred = state.preferred,
        showPackage = state.showPackage
    )
}
