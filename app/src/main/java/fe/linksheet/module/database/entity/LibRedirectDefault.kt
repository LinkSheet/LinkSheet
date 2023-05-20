package fe.linksheet.module.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lib_redirect_default")
data class LibRedirectDefault(
    @PrimaryKey
    val serviceKey: String,
    val frontendKey: String,
    val instanceUrl: String,
) {
    companion object {
        const val libRedirectRandomInstanceKey = "RANDOM_INSTANCE"
    }
}
