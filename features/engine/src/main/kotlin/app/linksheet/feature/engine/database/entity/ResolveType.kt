package app.linksheet.feature.engine.database.entity

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.db.SupportSQLiteDatabase
import app.linksheet.api.database.IKnown
import app.linksheet.api.database.KnownHolder
import app.linksheet.api.database.insert

@Entity(tableName = "resolve_type")
data class ResolveType(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
) : IKnown {

    companion object : KnownHolder<ResolveType> {
        val FollowRedirects = ResolveType(1, "follow_redirects")
        val Amp2Html = ResolveType(2, "amp2html")

        override val items = listOf(FollowRedirects, Amp2Html)

        override fun initialize(db: SupportSQLiteDatabase) {
            for (item in items) {
                db.insert("resolve_type", SQLiteDatabase.CONFLICT_IGNORE, item.toContentValues())
            }
        }

        override fun initialize(connection: SQLiteConnection) {
            for (item in items) {
                connection.insert("resolve_type", SQLiteDatabase.CONFLICT_IGNORE, item.toContentValues())
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
