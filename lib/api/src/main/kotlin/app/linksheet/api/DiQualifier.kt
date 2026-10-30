package app.linksheet.api

import org.koin.core.qualifier.qualifier

enum class Qualifier {
    LinkAssetsPreference,
    PrivacyIncognitoModePreference
}

object DiQualifier {
    val LinkAssetsPreference = Qualifier.LinkAssetsPreference.qualifier
    val PrivacyIncognitoModePreference = Qualifier.PrivacyIncognitoModePreference.qualifier
}
