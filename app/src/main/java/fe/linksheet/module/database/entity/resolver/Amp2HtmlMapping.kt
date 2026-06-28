package fe.linksheet.module.database.entity.resolver

import androidx.room3.ColumnInfo
import androidx.room3.Entity

@Entity(
    tableName = Amp2HtmlMapping.TABLE_NAME,
    primaryKeys = [Amp2HtmlMapping.COLUMN_AMP_URL]
)
data class Amp2HtmlMapping(
    @ColumnInfo(name = COLUMN_AMP_URL) val ampUrl: String,
    val canonicalUrl: String? = null,
    @ColumnInfo(defaultValue = "'true'")
    val isCacheHit: Boolean = true
) {
    companion object {
        const val TABLE_NAME = "amp2html_mapping"
        const val COLUMN_AMP_URL = "ampUrl"
    }
}
