package fe.linksheet.activity.bottomsheet.dev.grid

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.linksheet.composable.util.defaultRoundedCornerShape
import fe.linksheet.module.resolver.LibRedirectResolver
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.util.PrivateBrowsingBrowser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GridBrowserButton(
    appInfo: DisplayActivityInfo,
    privateBrowser: PrivateBrowsingBrowser?,
    showPackage: Boolean,
    launchApp: (DisplayActivityInfo, Boolean, Boolean) -> Unit,
    libRedirectResult: LibRedirectResolver.LibRedirectResult.Redirected? = null,
    ignoreLibRedirectClick: ((LibRedirectResolver.LibRedirectResult.Redirected) -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .height(85.dp)
//            .border(1.dp, Color.Red)
            .padding(all = 7.dp)
            .clip(defaultRoundedCornerShape)
            .clickable { launchApp(appInfo, false, false) }
            .padding(all = 3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            bitmap = appInfo.iconBitmap,
            contentDescription = appInfo.label,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = appInfo.label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        if (showPackage) {
            Text(
                text = appInfo.packageName,
                fontSize = 12.sp,
                lineHeight = 12.sp
            )
        }
    }
}
