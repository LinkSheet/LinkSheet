package app.linksheet.api

import org.koin.core.qualifier.qualifier

enum class Qualifier {
    LinkAssetsPreference
}

object DiQualifier {
    val LinkAssetsPreference = Qualifier.LinkAssetsPreference.qualifier
}
