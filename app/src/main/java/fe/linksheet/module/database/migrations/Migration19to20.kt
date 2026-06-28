package fe.linksheet.module.database.migrations

import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import app.linksheet.feature.engine.database.entity.CachedHtml
import app.linksheet.feature.engine.database.entity.PreviewCache
import app.linksheet.feature.engine.database.entity.ResolvedUrl

object Migration19to20 : Migration(19, 20) {

    override suspend fun migrate(connection: SQLiteConnection) = connection.run {
        execSQL("DROP TABLE `${CachedHtml.TABLE_NAME}`")
        execSQL("DROP TABLE `${PreviewCache.TABLE_NAME}`")
        execSQL("DROP TABLE `${ResolvedUrl.TABLE_NAME}`")
        execSQL("DROP TABLE `resolve_type`")
        execSQL("DROP TABLE `url`")
    }
}
