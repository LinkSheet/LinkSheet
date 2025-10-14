package fe.linksheet.module.database.dao.base

import androidx.room.Query
import app.linksheet.api.database.BaseDao

abstract class PackageEntityDao<T : PackageEntity<T>, C : PackageEntityCreator<T>>(
    private val creator: C
) : BaseDao<T> {
    @Query("")
    abstract suspend fun deleteByPackageName(packageName: String)

    enum class Mode(private val bool: Boolean) {
        Insert(true), Delete(false);

        companion object {
            fun fromBool(bool: Boolean) = entries.find { it.bool == bool }!!
        }
    }

    suspend fun insertOrDelete(mode: Mode, packageName: String) {
        when (mode) {
            Mode.Insert -> insert(creator.createInstance(packageName))
            Mode.Delete -> deleteByPackageName(packageName)
        }
    }
}

abstract class PackageEntity<T>(open val packageName: String)

interface PackageEntityCreator<T : PackageEntity<T>> {
    fun createInstance(packageName: String): T
}
