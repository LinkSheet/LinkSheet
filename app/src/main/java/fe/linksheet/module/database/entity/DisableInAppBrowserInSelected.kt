package fe.linksheet.module.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey
import fe.linksheet.module.database.dao.base.PackageEntity
import fe.linksheet.module.database.dao.base.PackageEntityCreator

@Entity(tableName = "disable_in_app_browser_in_selected", indices = [Index("packageName", unique = true)])
data class DisableInAppBrowserInSelected(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id") val id: Int = 0,
    override val packageName: String
) : PackageEntity<DisableInAppBrowserInSelected>(packageName) {
    companion object Creator : PackageEntityCreator<DisableInAppBrowserInSelected> {
        override fun createInstance(packageName: String) = DisableInAppBrowserInSelected(
            packageName = packageName
        )
    }
}
