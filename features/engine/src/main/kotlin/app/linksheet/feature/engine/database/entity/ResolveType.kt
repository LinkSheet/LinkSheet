package app.linksheet.feature.engine.database.entity

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.db.SupportSQLiteDatabase
import app.linksheet.api.database.IKnown
import app.linksheet.api.database.KnownHolder
import app.linksheet.api.database.insert

@Entity(tableName = ResolveType.TABLE_NAME)
data class ResolveType(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = COLUMN_ID) val id: Long = 0,
    @ColumnInfo(name = COLUMN_NAME) val name: String
) : IKnown {

    companion object : KnownHolder<ResolveType> {
        const val TABLE_NAME = "resolve_type"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"

        val FollowRedirects = ResolveType(1, "follow_redirects")
        val Amp2Html = ResolveType(2, "amp2html")

        override val items = listOf(FollowRedirects, Amp2Html)

        override fun initialize(db: SupportSQLiteDatabase) {
            for (item in items) {
                db.insert(TABLE_NAME, SQLiteDatabase.CONFLICT_IGNORE, item.toContentValues())
            }
        }

        override fun initialize(connection: SQLiteConnection) {
            for (item in items) {
                connection.insert(
                    TABLE_NAME,
                    SQLiteDatabase.CONFLICT_IGNORE,
                    item.toContentValues()
                )
            }
        }
    }

    override fun toContentValues(): ContentValues {
        return ContentValues().apply {
            put(COLUMN_ID, id)
            put(COLUMN_NAME, name)
        }
    }
}
