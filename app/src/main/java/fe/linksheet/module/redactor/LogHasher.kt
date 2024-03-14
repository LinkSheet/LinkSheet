package fe.linksheet.module.redactor

import android.content.ComponentName
import android.content.pm.ActivityInfo
import android.content.pm.ComponentInfo
import android.content.pm.ResolveInfo
import android.net.Uri
import fe.linksheet.extension.appendHashed
import fe.linksheet.module.database.entity.AppSelectionHistory
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.resolver.DisplayActivityInfo
import fe.stringbuilder.util.*
import javax.crypto.Mac

sealed interface LogHasher {
    data object NoOpHasher : LogHasher {
        override fun <T> hash(
            stringBuilder: StringBuilder,
            input: T,
            hashProcessor: HashProcessor<T>,
        ): StringBuilder = stringBuilder.append(input)
    }

    class LogKeyHasher(private val hmac: Mac) : LogHasher {
        override fun <T> hash(
            stringBuilder: StringBuilder,
            input: T,
            hashProcessor: HashProcessor<T>,
        ) = hashProcessor.process(stringBuilder, input, hmac)
    }


    fun <T> hash(
        stringBuilder: StringBuilder,
        input: T,
        hashProcessor: HashProcessor<T>,
    ): StringBuilder

    fun <T> hash(
        stringBuilder: StringBuilder,
        prefix: String,
        input: T,
        hashProcessor: HashProcessor<T>,
    ) = stringBuilder.apply {
        append(prefix)
        hash(this, input, hashProcessor)
    }
}

typealias PackageProcessor = HashProcessor.StringProcessor
typealias HostProcessor = HashProcessor.StringProcessor
typealias FileNameProcessor = HashProcessor.StringProcessor
typealias FileExtensionProcessor = HashProcessor.StringProcessor

sealed interface HashProcessor<T> {
    data object NoOpProcessor : HashProcessor<String> {
        override fun process(
            stringBuilder: StringBuilder,
            input: String,
            mac: Mac,
        ): StringBuilder = stringBuilder.append(input)
    }

    data object UriProcessor : HashProcessor<Uri> {

        override fun process(
            stringBuilder: StringBuilder,
            input: Uri,
            mac: Mac,
        ): StringBuilder = stringBuilder.append(buildHashedUriString(input.toString(), mac))
    }

    data object UrlProcessor : HashProcessor<String> {
        override fun process(
            stringBuilder: StringBuilder,
            input: String,
            mac: Mac,
        ): StringBuilder = stringBuilder.append(buildHashedUriString(input, mac))
    }

    data object StringProcessor : HashProcessor<String?> {
        override fun process(
            stringBuilder: StringBuilder,
            input: String?,
            mac: Mac,
        ): StringBuilder = stringBuilder.appendHashed(mac, input)
    }

    data object ComponentProcessor : HashProcessor<ComponentName> {
        override fun process(
            stringBuilder: StringBuilder,
            input: ComponentName,
            mac: Mac,
        ) = stringBuilder.curlyWrapped {
            slashSeparated {
                item { StringProcessor.process(stringBuilder, input.packageName, mac) }
                item { appendHashed(mac, input.className) }
            }
        }
    }

    data object PreferenceAppHashProcessor : HashProcessor<PreferredApp> {
        override fun process(
            stringBuilder: StringBuilder,
            input: PreferredApp,
            mac: Mac,
        ) = stringBuilder.commaSeparated {
            item {
                append("host=")
                UrlProcessor.process(stringBuilder, input.host, mac)
            }
            itemNotNull(input.packageName) {
                append("pkg=")
                StringProcessor.process(this, input.packageName!!, mac)
            }
            item {
                ComponentProcessor.process(stringBuilder, input.componentName!!, mac)
            }
            item {
                append("alwaysPreferred=", input.alwaysPreferred)
            }
        }
    }

    data object AppSelectionHistoryHashProcessor : HashProcessor<AppSelectionHistory> {
        override fun process(
            stringBuilder: StringBuilder,
            input: AppSelectionHistory,
            mac: Mac,
        ) = stringBuilder.commaSeparated {
            item {
                append("host=")
                UrlProcessor.process(stringBuilder, input.host, mac)
            }

            item {
                append("lastUsed=", input.lastUsed)
            }
        }
    }

    data object ActivityInfoProcessor : HashProcessor<ActivityInfo> {
        override fun process(
            stringBuilder: StringBuilder,
            input: ActivityInfo,
            mac: Mac,
        ): StringBuilder = stringBuilder.appendHashed(mac, input.name)
    }

    data object ActivityInfoListProcessor : HashProcessor<List<ActivityInfo>> {
        override fun process(stringBuilder: StringBuilder, input: List<ActivityInfo>, mac: Mac): StringBuilder {
            return stringBuilder.wrapped(Bracket.Curly) {
                separated(Separator.Comma) {
                    input.forEach {
                        item { ActivityInfoProcessor.process(stringBuilder, it, mac) }
                    }
                }
            }
        }
    }

    data object DisplayActivityInfoProcessor : HashProcessor<DisplayActivityInfo> {
        override fun process(stringBuilder: StringBuilder, input: DisplayActivityInfo, mac: Mac): StringBuilder {
            return stringBuilder.wrapped(Bracket.Curly) {
                separated(Separator.Comma) {
                    item("activityInfo=") { ActivityInfoProcessor.process(stringBuilder, input.activityInfo, mac) }
                    item("label=") { StringProcessor.process(stringBuilder, input.label, mac) }
                    itemNotNull(input.extendedInfo != null, "extendedInfo=") {
                        StringProcessor.process(
                            stringBuilder,
                            input.extendedInfo.toString(),
                            mac
                        )
                    }
                    item("resolveInfo=") { ResolveInfoProcessor.process(stringBuilder, input.resolvedInfo, mac) }
                }
            }
        }
    }

    data object DisplayActivityInfoListProcessor : HashProcessor<List<DisplayActivityInfo>> {
        override fun process(stringBuilder: StringBuilder, input: List<DisplayActivityInfo>, mac: Mac): StringBuilder {
            return stringBuilder.wrapped(Bracket.Square) {
                separated(Separator.Comma) {
                    input.forEach {
                        item { DisplayActivityInfoProcessor.process(stringBuilder, it, mac) }
                    }
                }
            }
        }
    }

    data object ResolveInfoProcessor : HashProcessor<ResolveInfo> {
        override fun process(
            stringBuilder: StringBuilder,
            input: ResolveInfo,
            mac: Mac,
        ) = stringBuilder.apply {
            val packageName = input.getComponentInfo()?.packageName
            PackageProcessor.process(this, packageName ?: "", mac)
        }

        private fun ResolveInfo.getComponentInfo(): ComponentInfo? {
            if (activityInfo != null) return activityInfo
            if (serviceInfo != null) return serviceInfo
            if (providerInfo != null) return providerInfo

            return null
        }
    }

    data object ResolveInfoListProcessor : HashProcessor<List<ResolveInfo>> {
        override fun process(stringBuilder: StringBuilder, input: List<ResolveInfo>, mac: Mac): StringBuilder {
            return stringBuilder.wrapped(Bracket.Square) {
                separated(Separator.Comma) {
                    input.forEach {
                        item { ResolveInfoProcessor.process(stringBuilder, it, mac) }
                    }
                }
            }
        }
    }

    fun process(stringBuilder: StringBuilder, input: T, mac: Mac): StringBuilder
}
