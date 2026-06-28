package app.linksheet.feature.libredirect.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = LibRedirectServiceState.TABLE_NAME)
data class LibRedirectServiceState(
    @PrimaryKey
    val serviceKey: String,
    var enabled: Boolean
) {
    companion object {
        const val TABLE_NAME = "lib_redirect_service_state"
    }
}
