package fe.linksheet.data.dao.base

import androidx.room.Query

abstract class PackageEntityDao<T, C : PackageEntityCreator<T>>(private val creator: C) : BaseDao<T> {
    @Query("")
    abstract fun deleteByPackageName(packageName: String)

    enum class Mode(private val bool: Boolean) {
        Insert(true), Delete(false);

        companion object {
            fun fromBool(bool: Boolean) = Mode.values().find { it.bool == bool }!!
        }
    }

    suspend fun insertOrDelete(mode: Mode, packageName: String) {
        when (mode) {
            Mode.Insert -> insert(creator.createInstance(packageName))
            Mode.Delete -> deleteByPackageName(packageName)
        }
    }
}

interface PackageEntityCreator<T> {
    fun createInstance(packageName: String): T
}