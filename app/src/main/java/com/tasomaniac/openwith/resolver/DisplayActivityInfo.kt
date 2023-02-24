package com.tasomaniac.openwith.resolver

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.compose.ui.graphics.asImageBitmap
import com.tasomaniac.openwith.data.PreferredApp
import com.tasomaniac.openwith.extension.componentName
import fe.linksheet.util.getBitmapFromImage
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class DisplayActivityInfo(
    val activityInfo: ActivityInfo,
    val displayLabel: String,
    val extendedInfo: CharSequence? = null
) : Parcelable {

    @IgnoredOnParcel
    var displayIcon: Drawable? = null

    @IgnoredOnParcel
    val packageName: String = activityInfo.packageName

    @IgnoredOnParcel
    val componentName by lazy {
        activityInfo.componentName()
    }

    @IgnoredOnParcel
    val flatComponentName by lazy {
        componentName.flattenToString()
    }

    fun intentFrom(sourceIntent: Intent): Intent {
        return Intent(sourceIntent)
            .setComponent(componentName)
            .addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT or Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP)
    }

    fun toPreferredApp(host: String, alwaysPreferred: Boolean) =
        PreferredApp(host = host, component = componentName.flattenToString(), alwaysPreferred = alwaysPreferred)

    fun getBitmap(context: Context) = getBitmapFromImage(context, displayIcon!!).asImageBitmap()

    override fun equals(other: Any?): Boolean {
        return (other as? DisplayActivityInfo)?.componentName == componentName
    }

    override fun hashCode(): Int = componentName.hashCode()
}
