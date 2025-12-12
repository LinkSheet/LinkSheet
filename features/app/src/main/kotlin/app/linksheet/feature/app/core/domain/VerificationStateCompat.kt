package app.linksheet.feature.app.core.domain

sealed interface VerificationStateCompat

data class VerificationState(
    val hostToStateMap: Map<String, Int>,
    val isLinkHandlingAllowed: Boolean,
) : VerificationStateCompat

data object VerificationBrowserState : VerificationStateCompat

data object VerificationUnsupportedState : VerificationStateCompat
