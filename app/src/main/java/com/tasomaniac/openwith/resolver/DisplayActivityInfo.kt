package com.tasomaniac.openwith.resolver

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.Drawable
import android.os.Parcelable
import com.tasomaniac.openwith.extension.componentName
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

    fun packageName(): String = activityInfo.packageName

    fun intentFrom(sourceIntent: Intent): Intent {
        return Intent(sourceIntent)
            .setComponent(componentName())
            .addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT or Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP)
    }

    private fun componentName() = activityInfo.componentName()

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is DisplayActivityInfo) {
            return false
        }
        return componentName() == other.componentName()
    }

    override fun hashCode(): Int = componentName().hashCode()
}
