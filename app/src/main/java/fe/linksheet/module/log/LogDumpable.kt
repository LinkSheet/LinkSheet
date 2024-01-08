package fe.linksheet.module.log

import android.content.ComponentName
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import fe.kotlin.extension.iterable.forEachWithInfo
import fe.libredirectkt.LibRedirectService
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.log.LogDumpable.Companion.dumpObject
import fe.stringbuilder.util.commaSeparated
import fe.stringbuilder.util.curlyWrapped
import fe.stringbuilder.util.squareWrapped

interface LogDumpable {
    fun dump(stringBuilder: StringBuilder, hasher: LogHasher): StringBuilder

    companion object {
        fun dumpObject(
            prefix: String,
            stringBuilder: StringBuilder,
            hasher: LogHasher,
            obj: Any?
        ) = stringBuilder.apply {
            append(prefix)
            dumpObject(this, hasher, obj)
        }

        fun dumpObject(stringBuilder: StringBuilder, hasher: LogHasher, obj: Any?): StringBuilder? {
            if (obj == null) return stringBuilder

            if (obj is String) return stringBuilder.append(obj)
            if (obj is Boolean) return stringBuilder.append(obj)
            if (obj is LogDumpable) return obj.dump(stringBuilder, hasher)

            if (obj is List<*>) {
                return stringBuilder.squareWrapped {
                    obj.forEachWithInfo { element, _, _, last ->
                        val result = dumpObject(this, hasher, element)
                        if (result != null && !last) {
                            this.append(",")
                        }
                    }
                }
            }

            return when (obj) {
                is ActivityInfo -> ActivityInfoDumpable.dump(stringBuilder, obj, hasher)
                is Intent -> IntentDumpable.dump(stringBuilder, obj, hasher)
                is ResolveInfo -> ResolveInfoDumpable.dump(stringBuilder, obj, hasher)
                is ComponentName -> ComponentNameDumpable.dump(stringBuilder, obj, hasher)
                is LibRedirectService -> LibRedirectServiceDumpable.dump(stringBuilder, obj, hasher)
                else -> null
            }
        }

        fun <T> dumpObject(
            stringBuilder: StringBuilder,
            hasher: LogHasher,
            obj: T,
            hashProcessor: HashProcessor<T>
        ) = hasher.hash(stringBuilder, obj, hashProcessor)
    }
}

interface LogDumpableWrapper<T> {
    fun dump(
        stringBuilder: StringBuilder,
        instance: T,
        hasher: LogHasher
    ): StringBuilder
}

object ActivityInfoDumpable : LogDumpableWrapper<ActivityInfo> {
    override fun dump(
        stringBuilder: StringBuilder,
        instance: ActivityInfo,
        hasher: LogHasher
    ) = hasher.hash(stringBuilder, instance, HashProcessor.ActivityInfoProcessor)
}

object IntentDumpable : LogDumpableWrapper<Intent> {
    override fun dump(
        stringBuilder: StringBuilder,
        instance: Intent,
        hasher: LogHasher
    ) = stringBuilder.commaSeparated {
        item { append("act=", instance.action) }
        itemNotNull(instance.categories) {
            append("cat=", instance.categories)
        }
        itemNotNull(instance.component) {
            dumpObject("cmp=", this, hasher, instance.component!!)
        }
        item {
            append("flags=", instance.flags)
        }
        itemNotNull(instance.`package`) {
            hasher.hash(this, "pkg=", instance.`package`!!, PackageProcessor)
        }
    }
}

object ResolveInfoDumpable : LogDumpableWrapper<ResolveInfo> {
    override fun dump(
        stringBuilder: StringBuilder,
        instance: ResolveInfo,
        hasher: LogHasher
    ) = hasher.hash(stringBuilder, instance, HashProcessor.ResolveInfoProcessor)
}

object ComponentNameDumpable : LogDumpableWrapper<ComponentName> {
    override fun dump(
        stringBuilder: StringBuilder,
        instance: ComponentName,
        hasher: LogHasher
    ) = hasher.hash(stringBuilder, instance, HashProcessor.ComponentProcessor)
}
object PreferredAppDumpable : LogDumpableWrapper<PreferredApp> {
    override fun dump(
        stringBuilder: StringBuilder,
        instance: PreferredApp,
        hasher: LogHasher
    ) = stringBuilder.commaSeparated {
        item {
            hasher.hash(this, "host=", instance.host, HashProcessor.UrlProcessor)
        }

        itemNotNull(instance.packageName) {
            hasher.hash(this, "pkg=", instance.packageName!!, PackageProcessor)
        }

        item {
            hasher.hash(
                this,
                "componentName=",
                instance.componentName!!,
                HashProcessor.ComponentProcessor
            )
        }
    }
}

object LibRedirectServiceDumpable : LogDumpableWrapper<LibRedirectService> {
    override fun dump(
        stringBuilder: StringBuilder,
        instance: LibRedirectService,
        hasher: LogHasher
    ) = hasher.hash(stringBuilder, instance.key, HashProcessor.NoOpProcessor)
}


data class MapDumpable<K, V, DK, DV>(
    val map: Map<K, V>,
    val keyName: String,
    val valueName: String,
    val keySelector: (K) -> DK,
    val valueSelector: (V) -> DV,
    val keyProcessor: HashProcessor<DK>,
    val valueProcessor: HashProcessor<DV>
) : LogDumpable {
    override fun dump(
        stringBuilder: StringBuilder,
        hasher: LogHasher
    ) = stringBuilder.commaSeparated {
        map.forEach { (key, value) ->
            item {
                curlyWrapped {
                    hasher.hash(this, "$keyName=", keySelector(key), keyProcessor)
                    append(",")
                    hasher.hash(this, "$valueName=", valueSelector(value), valueProcessor)
                }
            }
        }
    }
}

fun <K, V, DK, DV> Map<K, V>.toDumpable(
    keyName: String,
    valueName: String,
    keySelector: (K) -> DK,
    valueSelector: (V) -> DV,
    keyProcessor: HashProcessor<DK>,
    valueProcessor: HashProcessor<DV>
) = MapDumpable(this, keyName, valueName, keySelector, valueSelector, keyProcessor, valueProcessor)
