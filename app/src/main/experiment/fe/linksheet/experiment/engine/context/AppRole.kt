package fe.linksheet.experiment.engine.context

import fe.kotlin.extension.iterable.mapToSet
import fe.linksheet.util.AndroidAppPackage

enum class AppRoleId {
    Browser,
    NativeApp
}

data class AppRole(val id: AppRoleId, val app: AndroidAppPackage)

fun Set<AppRole>.findRoleOrNull(id: AppRoleId): Set<AndroidAppPackage> {
    return filter { it.id == id }.mapToSet { it.app }
}

fun MutableSet<AppRole>.removeAll(id: AppRoleId): Boolean {
    return removeAll { it.id == id }
}
