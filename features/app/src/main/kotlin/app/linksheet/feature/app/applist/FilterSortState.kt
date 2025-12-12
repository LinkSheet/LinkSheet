package app.linksheet.feature.app.applist

enum class SortType {
    AZ,
    InstallTime,
}

enum class StateModeFilter {
    ShowAll,
    EnabledOnly,
    DisabledOnly
}

enum class TypeFilter {
    All,
    Browser,
    Native
}

data class FilterState(
    val mode: StateModeFilter,
    val type: TypeFilter,
    val systemApps: Boolean
)

data class SortByState(
    val sort: SortType,
    val ascending: Boolean,
)
