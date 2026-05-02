package app.linksheet.feature.libredirect.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

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
