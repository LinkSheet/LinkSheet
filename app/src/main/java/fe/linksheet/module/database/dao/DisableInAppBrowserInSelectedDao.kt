package fe.linksheet.module.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.api.database.UserDataDao
import fe.linksheet.module.database.entity.DisableInAppBrowserInSelected
import kotlinx.coroutines.flow.Flow

@Dao
interface DisableInAppBrowserInSelectedDao : BaseDao<DisableInAppBrowserInSelected>, UserDataDao {
    @Query("SELECT * FROM ${DisableInAppBrowserInSelected.TABLE_NAME}")
    override fun getAll(): Flow<List<DisableInAppBrowserInSelected>>

    @Query("DELETE FROM ${DisableInAppBrowserInSelected.TABLE_NAME}")
    override suspend fun deleteAll()

    @Query("DELETE FROM ${DisableInAppBrowserInSelected.TABLE_NAME} WHERE packageName = :packageName")
    suspend fun deleteByPackageOrComponentName(packageName: String)
}
