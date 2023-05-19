package fe.linksheet.module.repository

import com.tasomaniac.openwith.data.PreferredApp
import com.tasomaniac.openwith.data.PreferredAppDao

class PreferredAppRepository(val dao: PreferredAppDao) {
    fun getAllAlwaysPreferred() = dao.getAllAlwaysPreferred()

    suspend fun deleteByPackageName(packageName: String) = dao.deleteByPackageName(packageName)

    suspend fun deleteByHostAndPackageName(
        host: String,
        packageName: String
    ) = dao.deleteByHostAndPackageName(host, packageName)

    suspend fun insert(preferredApp: PreferredApp) = dao.insert(preferredApp)
    suspend fun insert(items: List<PreferredApp>) = dao.insert(items)
}