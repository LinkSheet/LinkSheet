package app.linksheet.feature.libredirect.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lib_redirect_default")
data class LibRedirectDefault(
    @PrimaryKey
    val serviceKey: String,
    val frontendKey: String,
    val instanceUrl: String,
    @ColumnInfo(defaultValue = "0")
    val version: Int = 0,
    @ColumnInfo(defaultValue = "'false'")
    val userDefined: Boolean = false,
) {
    companion object {
        const val randomInstance = "RANDOM_INSTANCE"
        const val IgnoreIntentKey = "IGNORE_LIBREDIRECT"
    }
}
