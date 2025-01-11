package fe.linksheet.module.app.domain

interface VerificationStateCompat

data class VerificationState(
    val hostToStateMap: Map<String, Int>,
    val isLinkHandlingAllowed: Boolean,
) : VerificationStateCompat

data object VerificationUnsupportedState : VerificationStateCompat
