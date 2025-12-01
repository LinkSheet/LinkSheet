package fe.linksheet.module.viewmodel.common

data class FilterState(
    val mode: StateModeFilter,
    val type: TypeFilter,
    val systemApps: Boolean
)
