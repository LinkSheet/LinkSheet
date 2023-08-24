package fe.linksheet.module.database.entity

import android.content.ComponentName
import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import fe.linksheet.resolver.PreferredDisplayActivityInfo
import fe.linksheet.extension.android.queryFirstIntentActivityByPackageNameOrNull
import fe.linksheet.extension.android.toDisplayActivityInfo
import fe.linksheet.module.log.HostProcessor
import fe.linksheet.module.log.LogDumpable
import fe.linksheet.module.log.LogDumpable.Companion.dumpObject
import fe.linksheet.module.log.LogHasher
import fe.linksheet.module.log.PackageProcessor
import fe.stringbuilder.util.commaSeparated
import java.lang.StringBuilder

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
) : LogDumpable {
    @delegate:Ignore
    val componentName by lazy {
        ComponentName.unflattenFromString(component) ?:
        ComponentName.unflattenFromString("$packageName/$component")
    }

    fun toDisplayActivityInfo(context: Context) = context.packageManager
        .queryFirstIntentActivityByPackageNameOrNull(packageName!!)
        ?.toDisplayActivityInfo(context)

    fun toPreferredDisplayActivityInfo(context: Context) = toDisplayActivityInfo(context)?.let {
        PreferredDisplayActivityInfo(this, it)
    }

    override fun dump(
        stringBuilder: StringBuilder,
        hasher: LogHasher
    ) = stringBuilder.commaSeparated {
        item {
            hasher.hash(this, "host=", host, HostProcessor)
        }
        itemNotNull(packageName) {
            hasher.hash(
                this,
                "pkg=",
                packageName!!,
                PackageProcessor
            )
        }
        item {
            dumpObject("cmp=", stringBuilder, hasher, componentName)
        }
        item {
            append("alwaysPreferred=", alwaysPreferred)
        }
    }

    override fun equals(other: Any?): Boolean {
        return (other as? PreferredApp)?.component == this.component
    }

    override fun hashCode(): Int {
        return component.hashCode()
    }
}
