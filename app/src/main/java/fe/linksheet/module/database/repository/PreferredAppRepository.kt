package fe.linksheet.module.database.repository

import com.tasomaniac.openwith.data.LinkSheetDatabase
import com.tasomaniac.openwith.data.PreferredApp
import com.tasomaniac.openwith.data.PreferredAppDao
import org.koin.dsl.module

val preferredAppRepositoryModule = module {
    single {
        PreferredAppRepository(get<LinkSheetDatabase>().preferredAppDao())
    }
}

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