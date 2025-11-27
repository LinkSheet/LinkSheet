package fe.linksheet.module.database.dao.base

import androidx.room.Query
import kotlinx.coroutines.flow.Flow

abstract class WhitelistedBrowsersDao<T : PackageEntity<T>, C : PackageEntityCreator<T>>(
    creator: C
) : PackageEntityDao<T, C>(creator) {
    @Query("")
    abstract fun getAll(): Flow<List<T>>

    @Query("")
    abstract override suspend fun deleteByPackageOrComponentName(packageName: String)
}
