package fe.linksheet.module.resolver

import fe.android.preference.helper.OptionTypeMapper

sealed class FollowRedirectsMode(val name: String) {
    data object Auto : FollowRedirectsMode("auto")
    data object Manual : FollowRedirectsMode("manual")

    companion object : OptionTypeMapper<FollowRedirectsMode, String>(
        key = { it.name },
        options = { arrayOf(Auto, Manual) }
    )
}
