package com.tasomaniac.openwith.resolver

data class IntentResolverResult(
    val resolved: List<DisplayActivityInfo>,
    val filteredItem: DisplayActivityInfo?,
    val showExtended: Boolean,
    val alwaysPreferred: Boolean?,
    val selectedBrowserIsSingleOption: Boolean = false
) {
    val isEmpty get() = totalCount() == 0

    fun totalCount() = resolved.size + if (filteredItem != null) 1 else 0
}
