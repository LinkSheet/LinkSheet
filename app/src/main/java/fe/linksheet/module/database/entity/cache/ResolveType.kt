package fe.linksheet.module.database.entity.cache

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.sqlite.db.SupportSQLiteDatabase
import fe.linksheet.module.database.dao.base.IKnown
import fe.linksheet.module.database.dao.base.KnownHolder
import fe.linksheet.module.database.dao.cache.ResolveTypeDao

@Entity(tableName = "resolve_type")
data class ResolveType(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
) : IKnown {

    companion object : KnownHolder<ResolveType> {
        val REDIRECT = ResolveType(1, "redirect")
        val AMP2HTML = ResolveType(2, "amp")

        override val items = listOf(REDIRECT, AMP2HTML)

        override fun initialize(db: SupportSQLiteDatabase) {
            for (item in items) {
                db.insert("resolve_type", SQLiteDatabase.CONFLICT_IGNORE, item.toContentValues())
            }
        }
    }

    override fun toContentValues(): ContentValues {
        return ContentValues().apply {
            put("id", id)
            put("name", name)
        }
    }
}
