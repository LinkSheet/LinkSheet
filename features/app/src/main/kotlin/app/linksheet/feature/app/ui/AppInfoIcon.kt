package app.linksheet.feature.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.linksheet.feature.app.core.IAppInfo
import app.linksheet.feature.app.ui.AppInfoIconDefaults.DefaultIconSize
import fe.composekit.component.icon.AppIconImage

object AppInfoIconDefaults {
    val DefaultIconSize = 32.dp
}

@Composable
fun AppInfoIcon(
    modifier: Modifier = Modifier,
    size: Dp = DefaultIconSize,
    appInfo: IAppInfo,
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
//        appInfo = PackageInfoFakes.Youtube.toActivityAppInfo(icon = BitmapIconPainter.bitmap(bitmap))
//    )
}
