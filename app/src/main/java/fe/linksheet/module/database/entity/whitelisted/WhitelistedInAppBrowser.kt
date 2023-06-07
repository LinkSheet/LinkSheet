package fe.linksheet.module.database.entity.whitelisted

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import fe.linksheet.module.database.dao.base.PackageEntityCreator

@Entity(tableName = "whitelisted_in_app_browser", indices = [Index("packageName", unique = true)])
data class WhitelistedInAppBrowser(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id") val id: Int = 0,
    override val packageName: String
) : WhitelistedBrowser<WhitelistedInAppBrowser>(packageName) {

    companion object Creator : PackageEntityCreator<WhitelistedInAppBrowser> {
        override fun createInstance(packageName: String) = WhitelistedInAppBrowser(
            packageName = packageName
        )
    }
}