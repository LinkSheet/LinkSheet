package fe.linksheet.module.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = DisableInAppBrowserInSelected.TABLE_NAME,
    indices = [Index("packageName", unique = true)]
)
data class DisableInAppBrowserInSelected(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id") val id: Int = 0,
    val packageName: String
) {
    companion object {
        const val TABLE_NAME = "disable_in_app_browser_in_selected"
    }
}
