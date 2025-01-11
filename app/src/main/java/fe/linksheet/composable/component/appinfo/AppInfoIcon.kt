package fe.linksheet.composable.component.appinfo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fe.composekit.component.CommonDefaults
import fe.composekit.component.icon.AppIconImage
import fe.linksheet.composable.component.appinfo.AppInfoIconDefaults.DefaultIconSize
import fe.linksheet.module.app.AppInfo

object AppInfoIconDefaults {
    val DefaultIconSize = 32.dp
}

@Composable
fun <T : AppInfo> AppInfoIcon(
    modifier: Modifier = CommonDefaults.BaseContentModifier,
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
    val icon = remember {
        lazy { ImageBitmap(32, 32) }
    }

    AppInfoIcon(
        modifier = Modifier,
        appInfo = AppInfo(packageName = "fe.linksheet", label = "LinkSheet", icon = icon)
    )
}
