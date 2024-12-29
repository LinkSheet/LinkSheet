package fe.linksheet.activity.bottomsheet

import fe.linksheet.module.resolver.KnownBrowser


sealed interface ClickType {
    data object Single : ClickType
    data object Double : ClickType
    data object Long : ClickType
}

sealed interface ClickModifier {
    data class Private(val browser: KnownBrowser) : ClickModifier
    data object Always : ClickModifier
    data object None : ClickModifier
}
