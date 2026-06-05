package app.linksheet.feature.backup.impl.core

sealed interface ExportType
sealed interface PreferenceType : ExportType {
    val name: String

    object Preferences : PreferenceType {
        override val name: String = "preferences"
    }

    object Experiments : PreferenceType {
        override val name: String = "experiments"
    }

    object AppState : PreferenceType {
        override val name: String = "appState"
    }
}

sealed interface DatabaseType : ExportType {
    data object SelectionHistory : DatabaseType
    data object Cache : DatabaseType
}
