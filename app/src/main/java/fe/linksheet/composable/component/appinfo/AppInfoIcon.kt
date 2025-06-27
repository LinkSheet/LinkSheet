package fe.linksheet.composable.component.appinfo

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fe.composekit.component.icon.AppIconImage
import fe.linksheet.composable.component.appinfo.AppInfoIconDefaults.DefaultIconSize
import fe.linksheet.feature.app.AppInfo

object AppInfoIconDefaults {
    val DefaultIconSize = 32.dp
}

@Composable
fun <T : AppInfo> AppInfoIcon(
    modifier: Modifier = Modifier,
    size: Dp = DefaultIconSize,
    appInfo: T,
) {
    val icon = appInfo.icon
    if (icon != null) {
        AppIconImage(
            modifier = modifier,
            size = size,
            icon = icon,
            label = appInfo.label,
        )
    }
}

@Preview(showBackground = true, apiLevel = 31)
@Composable
private fun AppInfoIconPreview() {
//    val bitmap = drawBitmap(Size(24f, 24f)) {
//        drawCircle(Color.Red)
//    }
//
//    AppInfoIcon(
//        appInfo = PackageInfoFakes.Youtube.toActivityAppInfo(lazy { bitmap })
//    )
}
