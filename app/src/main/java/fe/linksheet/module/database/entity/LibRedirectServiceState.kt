package fe.linksheet.module.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lib_redirect_service_state")
data class LibRedirectServiceState(
    @PrimaryKey
    val serviceKey: String,
    var enabled: Boolean
)