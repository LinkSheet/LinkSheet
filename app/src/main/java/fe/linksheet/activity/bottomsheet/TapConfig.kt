package fe.linksheet.activity.bottomsheet

import androidx.annotation.StringRes
import fe.android.preference.helper.OptionTypeMapper
import fe.linksheet.R
import fe.linksheet.util.StringResHolder

sealed class TapConfig(val name: String, @StringRes stringRes: Int) : StringResHolder {
    override val id = stringRes

    data object None : TapConfig("none", R.string.no_action)
    data object OpenApp : TapConfig("open_app", R.string.open_app)
    data object SelectItem : TapConfig("select_item", R.string.select_app)
    data object OpenSettings : TapConfig("open_settings", R.string.open_app_settings_page)

    companion object : OptionTypeMapper<TapConfig, String>({ it.name }, {
        arrayOf(None, OpenApp, SelectItem, OpenSettings)
    })

    override fun toString(): String {
        return name
    }
}

