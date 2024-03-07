package fe.linksheet.module.database.entity

import android.content.ComponentName
import android.content.Context
import androidx.room.*
import fe.linksheet.extension.android.queryFirstIntentActivityByPackageNameOrNull
import fe.linksheet.extension.android.toDisplayActivityInfo
import fe.linksheet.module.redactor.*
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.resolver.PreferredDisplayActivityInfo
import fe.stringbuilder.util.commaSeparated
import java.lang.Package

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
) : Redactable<PreferredApp> {
    @delegate:Ignore
    val componentName by lazy {
        ComponentName.unflattenFromString(component) ?: ComponentName.unflattenFromString("$packageName/$component")
    }

    fun toDisplayActivityInfo(context: Context): DisplayActivityInfo? {
        return context.packageManager.queryFirstIntentActivityByPackageNameOrNull(packageName!!)
            ?.toDisplayActivityInfo(context)
    }

    fun toPreferredDisplayActivityInfo(context: Context): PreferredDisplayActivityInfo? {
        return toDisplayActivityInfo(context)?.let { info ->
            PreferredDisplayActivityInfo(this, info)
        }
    }

    override fun process(builder: StringBuilder, redactor: Redactor) = builder.commaSeparated {
//        item {
//            redactor.process(this, host, HostProcessor, "host=")
//        }
//        itemNotNull(packageName) {
//            redactor.process(this, packageName, PackageProcessor, "pkg=")
//        }
//        item {
//            redactor.process(this, componentName, HashProcessor.ComponentProcessor, "cmp=")
//        }
//        item {
//            append("alwaysPreferred=", alwaysPreferred)
//        }
    }

    override fun equals(other: Any?): Boolean {
        return (other as? PreferredApp)?.component == this.component
    }

    override fun hashCode(): Int {
        return component.hashCode()
    }
}
