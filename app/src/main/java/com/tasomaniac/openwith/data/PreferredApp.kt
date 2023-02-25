package com.tasomaniac.openwith.data

import android.content.ComponentName
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

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
    val componentName: ComponentName
        @Ignore get() = ComponentName.unflattenFromString(component)!!

    override fun equals(other: Any?): Boolean {
        return (other as? PreferredApp)?.component == this.component
    }

    override fun hashCode(): Int {
        return component.hashCode()
    }
}
