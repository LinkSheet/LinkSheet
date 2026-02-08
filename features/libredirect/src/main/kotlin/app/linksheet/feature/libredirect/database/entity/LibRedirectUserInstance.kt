package app.linksheet.feature.libredirect.database.entity

import androidx.room.Entity

@Entity(tableName = "lib_redirect_user_instance", primaryKeys = ["serviceKey", "frontendKey", "instanceUrl"])
data class LibRedirectUserInstance(
    val serviceKey: String,
    val frontendKey: String,
    val instanceUrl: String,
)
