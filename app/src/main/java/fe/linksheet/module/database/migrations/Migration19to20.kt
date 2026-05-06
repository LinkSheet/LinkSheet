package fe.linksheet.module.database.migrations

import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

object Migration19to20 : Migration(19, 20) {

    override suspend fun migrate(connection: SQLiteConnection) = connection.run {
        execSQL("DROP TABLE `html_cache`")
        execSQL("DROP TABLE `preview_cache`")
        execSQL("DROP TABLE `resolved_url`")
        execSQL("DROP TABLE `resolve_type`")
        execSQL("DROP TABLE `url`")
    }
}
