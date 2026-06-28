package app.linksheet.feature.libredirect.database.entity

import androidx.room3.Entity

@Entity(tableName = LibRedirectUserInstance.TABLE_NAME, primaryKeys = ["serviceKey", "frontendKey", "instanceUrl"])
data class LibRedirectUserInstance(
    val serviceKey: String,
    val frontendKey: String,
    val instanceUrl: String,
) {
    companion object{
        const val TABLE_NAME = "lib_redirect_user_instance"
    }
}
