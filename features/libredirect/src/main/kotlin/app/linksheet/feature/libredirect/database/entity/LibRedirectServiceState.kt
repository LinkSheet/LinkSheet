package app.linksheet.feature.libredirect.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "lib_redirect_service_state")
data class LibRedirectServiceState(
    @PrimaryKey
    val serviceKey: String,
    var enabled: Boolean
)
