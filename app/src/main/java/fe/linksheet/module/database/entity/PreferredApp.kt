package fe.linksheet.module.database.entity

import android.content.ComponentName
import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import fe.linksheet.resolver.PreferredDisplayActivityInfo
import fe.linksheet.extension.queryFirstIntentActivityByPackageNameOrNull
import fe.linksheet.extension.toDisplayActivityInfo

@Entity(
    tableName = "openwith",
    indices = [(Index("host", unique = true))]
)
data class PreferredApp(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id") val id: Int = 0,
    val host: String,
    val packageName: String? = null,
    val component: String,
    val alwaysPreferred: Boolean
) {
    @delegate:Ignore
    val componentName by lazy { ComponentName.unflattenFromString(component)!! }

    fun toDisplayActivityInfo(context: Context) = context.packageManager
        .queryFirstIntentActivityByPackageNameOrNull(packageName!!)
        ?.toDisplayActivityInfo(context)

    fun toPreferredDisplayActivityInfo(context: Context) = toDisplayActivityInfo(context)?.let {
        PreferredDisplayActivityInfo(this, it)
    }

    override fun equals(other: Any?): Boolean {
        return (other as? PreferredApp)?.component == this.component
    }

    override fun hashCode(): Int {
        return component.hashCode()
    }
}
