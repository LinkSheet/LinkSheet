package fe.linksheet.composable.component.appinfo

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.linksheet.testing.fake.PackageInfoFakes
import app.linksheet.testing.util.toActivityAppInfo
import fe.composekit.component.icon.AppIconImage
import fe.linksheet.composable.component.appinfo.AppInfoIconDefaults.DefaultIconSize
import fe.linksheet.module.app.AppInfo
import fe.linksheet.util.drawBitmap

object AppInfoIconDefaults {
    val DefaultIconSize = 32.dp
}

@Composable
fun <T : AppInfo> AppInfoIcon(
    modifier: Modifier = Modifier,
    size: Dp = DefaultIconSize,
    appInfo: T,
) {
    val icon = appInfo.icon?.value
    if (icon != null) {
        AppIconImage(
            modifier = modifier,
            size = size,
            bitmap = icon,
            label = appInfo.label,
        )
    }
}

@Preview(showBackground = true, apiLevel = 31)
@Composable
private fun AppInfoIconPreview() {
    val bitmap = drawBitmap(Size(24f, 24f)) {
        drawCircle(Color.Red)
    }

    AppInfoIcon(
        appInfo = PackageInfoFakes.Youtube.toActivityAppInfo(lazy { bitmap })
    )
}
