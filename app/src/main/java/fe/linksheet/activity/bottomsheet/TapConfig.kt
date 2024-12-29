package fe.linksheet.activity.bottomsheet

import android.os.Parcelable
import androidx.annotation.StringRes
import fe.android.preference.helper.OptionTypeMapper
import fe.linksheet.R
import fe.linksheet.util.StringResHolder
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class TapConfig(val name: String, @StringRes val stringRes: Int) : StringResHolder, Parcelable {
    @IgnoredOnParcel
    override val id = stringRes

    data object None : TapConfig("none", R.string.no_action)
    data object OpenApp : TapConfig("open_app", R.string.open_app)
    data object SelectItem : TapConfig("select_item", R.string.select_app)
    data object OpenSettings : TapConfig("open_settings", R.string.open_app_settings_page)

    companion object : OptionTypeMapper<TapConfig, String>(
        key = { it.name },
        options = { arrayOf(None, OpenApp, SelectItem, OpenSettings) }
    )

    override fun toString(): String {
        return name
    }
}

