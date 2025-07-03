package fe.linksheet.module.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration18to19 : Migration(18, 19) {

    override fun migrate(db: SupportSQLiteDatabase) = db.run {
        execSQL("CREATE TABLE IF NOT EXISTS `html_cache` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `content` TEXT NOT NULL, FOREIGN KEY(`id`) REFERENCES `url`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION )")
        execSQL("CREATE TABLE IF NOT EXISTS `preview_cache` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT, `description` TEXT, `faviconUrl` TEXT, `thumbnailUrl` TEXT, `resultId` INTEGER NOT NULL, FOREIGN KEY(`id`) REFERENCES `url`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION )")
        execSQL("CREATE TABLE IF NOT EXISTS `resolved_url` (`urlId` INTEGER NOT NULL, `typeId` INTEGER NOT NULL, `result` TEXT, PRIMARY KEY(`urlId`, `typeId`), FOREIGN KEY(`urlId`) REFERENCES `url`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION , FOREIGN KEY(`typeId`) REFERENCES `resolve_type`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION )")
        execSQL("CREATE INDEX IF NOT EXISTS `index_resolved_url_urlId` ON `resolved_url` (`urlId`)")
        execSQL("CREATE INDEX IF NOT EXISTS `index_resolved_url_typeId` ON `resolved_url` (`typeId`)")
        execSQL("CREATE TABLE IF NOT EXISTS `resolve_type` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL)")
        execSQL("CREATE TABLE IF NOT EXISTS `url` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestamp` INTEGER NOT NULL, `url` TEXT NOT NULL)")
        execSQL("CREATE TABLE IF NOT EXISTS `_new_resolved_redirect` (`shortUrl` TEXT NOT NULL, `resolvedUrl` TEXT, PRIMARY KEY(`shortUrl`))")

        val map = mutableMapOf<String, String>()
        query("SELECT `shortUrl`, `resolvedUrl` FROM `resolved_redirect`").use { cursor ->
            while (cursor.moveToNext()) {
                val shortUrl = cursor.getString(0)
                val resolvedUrl = cursor.getString(1)
                map[shortUrl] = resolvedUrl
            }
        }

        for ((shortUrl, resolvedUrl) in map) {
            execSQL("""INSERT INTO `_new_resolved_redirect` VALUES ('$shortUrl', '$resolvedUrl')""")
        }

        execSQL("DROP TABLE `resolved_redirect`")
        execSQL("ALTER TABLE `_new_resolved_redirect` RENAME TO `resolved_redirect`")
        execSQL("CREATE TABLE IF NOT EXISTS `_new_amp2html_mapping` (`ampUrl` TEXT NOT NULL, `canonicalUrl` TEXT, `isCacheHit` INTEGER NOT NULL DEFAULT 'true', PRIMARY KEY(`ampUrl`))")
        execSQL("INSERT INTO `_new_amp2html_mapping` (`ampUrl`,`canonicalUrl`,`isCacheHit`) SELECT `ampUrl`,`canonicalUrl`,`isCacheHit` FROM `amp2html_mapping`")
        execSQL("DROP TABLE `amp2html_mapping`")
        execSQL("ALTER TABLE `_new_amp2html_mapping` RENAME TO `amp2html_mapping`")
    }
}
