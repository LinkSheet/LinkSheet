package app.linksheet.feature.remoteconfig.ui

import app.linksheet.compose.list.item.PreferenceSwitchListItem
import app.linksheet.feature.remoteconfig.R
import fe.android.compose.text.AnnotatedStringResourceContent.Companion.annotatedStringResource
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.android.preference.helper.Preference
import fe.composekit.layout.column.SaneLazyColumnGroupScope
import fe.composekit.preference.ViewModelStatePreference

fun SaneLazyColumnGroupScope.remoteConfigListItem(
    statePreference: ViewModelStatePreference<Boolean, Boolean, Preference.Default<Boolean>>,
) {
    item(key = R.string.settings_remote_config__title) { padding, shape ->
        PreferenceSwitchListItem(
            statePreference = statePreference,
            shape = shape,
            padding = padding,
            headlineContent = textContent(R.string.settings_remote_config__title),
            supportingContent = annotatedStringResource(R.string.settings_remote_config__text_content),
        )
    }
}
