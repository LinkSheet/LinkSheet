package app.linksheet.feature.app.applist

import app.linksheet.feature.app.core.DomainVerificationAppInfo
import app.linksheet.feature.app.core.IAppInfo
import app.linksheet.feature.app.core.LinkHandling
import fe.linksheet.extension.android.SYSTEM_APP_FLAGS

enum class SortType {
    AZ,
    InstallTime,
}

enum class StateModeFilter {
    ShowAll,
    EnabledOnly,
    DisabledOnly
}

internal fun StateModeFilter.matches(info: DomainVerificationAppInfo): Boolean {
    return when (this) {
        StateModeFilter.ShowAll -> true
        StateModeFilter.EnabledOnly -> info.enabled
        StateModeFilter.DisabledOnly -> !info.enabled
    }
}

enum class TypeFilter {
    All,
    Browser,
    Native
}

internal fun TypeFilter.matches(info: DomainVerificationAppInfo): Boolean {
    return when (this) {
        TypeFilter.All -> true
        TypeFilter.Browser -> info.linkHandling == LinkHandling.Browser
        TypeFilter.Native -> info.linkHandling != LinkHandling.Browser
    }
}

data class FilterState(
    val mode: StateModeFilter,
    val type: TypeFilter,
    val systemApps: Boolean
) {
    companion object {
        val Default = FilterState(StateModeFilter.ShowAll, TypeFilter.All, true)
    }
}

fun FilterState.matches(info: IAppInfo): Boolean {
    if (!systemApps && info.flags in SYSTEM_APP_FLAGS) return false
    if (info is DomainVerificationAppInfo) {
        if (!mode.matches(info)) return false
        if (!type.matches(info)) return false
    }

    return true
}

data class SortByState(
    val sort: SortType,
    val ascending: Boolean,
) {
    companion object {
        val Default = SortByState(SortType.AZ, true)
    }
}

private val sortComparators = mapOf(
    SortType.InstallTime to IAppInfo.InstallTime,
    SortType.AZ to IAppInfo.Label
)

internal fun SortByState.toComparator(): Comparator<IAppInfo> {
    val (asc, desc) = sortComparators[sort]!!
    val comp = if (ascending) asc else desc
    return comp
}
