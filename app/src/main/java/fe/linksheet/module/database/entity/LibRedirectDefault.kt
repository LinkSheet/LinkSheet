package fe.linksheet.module.database.entity

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.room.Entity
import androidx.room.PrimaryKey
import fe.linksheet.R

@Entity(tableName = "lib_redirect_default")
data class LibRedirectDefault(
    @PrimaryKey
    val serviceKey: String,
    val frontendKey: String,
    val instanceUrl: String,
) {
    companion object {
        const val libRedirectRandomInstanceKey = "RANDOM_INSTANCE"
        const val libRedirectIgnore = "IGNORE_LIBREDIRECT"
    }
}
