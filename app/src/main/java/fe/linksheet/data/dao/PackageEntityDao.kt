package fe.linksheet.data.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

abstract class PackageEntityDao<T, C : PackageEntityCreator<T>>(private val creator: C) {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(entity: T)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(entities: List<T>)

    @Query("")
    abstract fun deleteByPackageName(packageName: String)

    enum class Mode(private val bool: Boolean) {
        Insert(true), Delete(false);

        companion object {
            fun fromBool(bool: Boolean) = Mode.values().find { it.bool == bool }!!
        }
    }

    fun insertOrDelete(mode: Mode, packageName: String) {
        when (mode) {
            Mode.Insert -> insert(creator.createInstance(packageName))
            Mode.Delete -> deleteByPackageName(packageName)
        }
    }
}