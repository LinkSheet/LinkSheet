package fe.linksheet.module.repository

import android.net.Uri
import fe.linksheet.module.database.dao.PreferredAppDao
import fe.linksheet.module.database.entity.PreferredApp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class PreferredAppRepository(private val dao: PreferredAppDao) {
    fun getAllAlwaysPreferred() = dao.getAllAlwaysPreferred()

    suspend fun getByHost(uri: Uri?) = uri?.let { dao.getByHost(it.host!!).firstOrNull() }

    suspend fun deleteByPackageName(packageName: String) = dao.deleteByPackageName(packageName)

    suspend fun deleteByPackageNames(packageNames: Set<String>) = dao.deleteByPackageName(packageNames)

    suspend fun deleteByHostAndPackageName(
        host: String,
        packageName: String,
    ) = dao.deleteByHostAndPackageName(host, packageName)

    suspend fun deleteByHost(host: String) {
        dao.deleteByHost(host)
    }

    suspend fun insert(preferredApp: PreferredApp) = dao.insert(preferredApp)
    suspend fun insert(items: List<PreferredApp>) = dao.insert(items)

    suspend fun getByPackageName(packageName: String) = dao.getByPackageName(packageName).first()
}
