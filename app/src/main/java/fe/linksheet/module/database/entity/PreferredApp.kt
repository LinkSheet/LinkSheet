package fe.linksheet.module.database.entity

import android.content.ComponentName
import androidx.room.*
import fe.linksheet.module.redactor.Redactable
import fe.linksheet.module.redactor.Redactor
import fe.stringbuilder.util.commaSeparated

@Entity(
    tableName = "openwith",
    indices = [(Index("host", unique = true))]
)
data class PreferredApp(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id") val id: Int = 0,
    val host: String,
    @ColumnInfo(name = "packageName") val _packageName: String? = null,
    @ColumnInfo(name = "component") val _component: String?,
    val alwaysPreferred: Boolean,
) : Redactable<PreferredApp> {
    @Ignore
    val cmp = tryGetComponentName(_component, _packageName)

    @Ignore
    val pkg = tryGetPackageName(cmp, _packageName)

    companion object {
        private fun tryGetComponentName(component: String?, packageName: String?): ComponentName? {
            if (component == null) return null
            val cmp = ComponentName.unflattenFromString(component)
            if (cmp != null) return cmp
            if (packageName == null) return null

            return ComponentName.unflattenFromString("$packageName/$component")
        }

        private fun tryGetPackageName(componentName: ComponentName?, packageName: String?): String? {
            if (componentName != null) return componentName.packageName
            return packageName
        }

        fun new(host: String, pkg: String, cmp: ComponentName?, always: Boolean): PreferredApp {
            return PreferredApp(
                host = host,
                _packageName = pkg,
                _component = cmp?.flattenToString(),
                alwaysPreferred = always,
            )
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
        if (other is PreferredApp) {
            return other.host == this.host && other._component == this._component && other._packageName == this._packageName
        }

        return false
    }

    override fun hashCode(): Int {
        var result = host.hashCode()
        result = 31 * result + (_packageName?.hashCode() ?: 0)
        result = 31 * result + (_component?.hashCode() ?: 0)
        return result
    }


}
