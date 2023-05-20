package fe.linksheet.module.repository

import android.net.Uri
import com.tasomaniac.openwith.data.PreferredApp
import com.tasomaniac.openwith.data.PreferredAppDao
import kotlinx.coroutines.flow.first

class PreferredAppRepository(private val dao: PreferredAppDao) {
    fun getAllAlwaysPreferred() = dao.getAllAlwaysPreferred()

    suspend fun getByHost(uri: Uri?) = uri?.let { dao.getByHost(it.host!!).first() }

    suspend fun deleteByPackageName(packageName: String) = dao.deleteByPackageName(packageName)

    suspend fun deleteByHostAndPackageName(
        host: String,
        packageName: String
    ) = dao.deleteByHostAndPackageName(host, packageName)

    suspend fun insert(preferredApp: PreferredApp) = dao.insert(preferredApp)
    suspend fun insert(items: List<PreferredApp>) = dao.insert(items)
}