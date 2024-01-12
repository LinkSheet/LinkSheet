package fe.linksheet.module.log.hasher

import android.content.ComponentName
import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import android.net.Uri
import fe.linksheet.extension.appendHashed
import fe.linksheet.module.database.entity.AppSelectionHistory
import fe.linksheet.module.database.entity.PreferredApp
import fe.stringbuilder.util.commaSeparated
import fe.stringbuilder.util.curlyWrapped
import fe.stringbuilder.util.slashSeparated
import javax.crypto.Mac

sealed interface LogHasher {
    data object NoOpHasher : LogHasher {
        override fun <T> hash(
            stringBuilder: StringBuilder,
            input: T,
            hashProcessor: HashProcessor<T>
        ): StringBuilder = stringBuilder.append(input)
    }

    class LogKeyHasher(private val hmac: Mac) : LogHasher {
        override fun <T> hash(
            stringBuilder: StringBuilder,
            input: T,
            hashProcessor: HashProcessor<T>
        ) = hashProcessor.process(stringBuilder, input, hmac)
    }


    fun <T> hash(
        stringBuilder: StringBuilder,
        input: T,
        hashProcessor: HashProcessor<T>
    ): StringBuilder

    fun <T> hash(
        stringBuilder: StringBuilder,
        prefix: String,
        input: T,
        hashProcessor: HashProcessor<T>
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
            mac: Mac
        ): StringBuilder = stringBuilder.append(input)
    }

    data object UriProcessor : HashProcessor<Uri> {

        override fun process(
            stringBuilder: StringBuilder,
            input: Uri,
            mac: Mac
        ): StringBuilder = stringBuilder.append(buildHashedUriString(input.toString(), mac))
    }

    data object UrlProcessor : HashProcessor<String> {
        override fun process(
            stringBuilder: StringBuilder,
            input: String,
            mac: Mac
        ): StringBuilder = stringBuilder.append(buildHashedUriString(input, mac))
    }

    data object StringProcessor : HashProcessor<String?> {
        override fun process(
            stringBuilder: StringBuilder,
            input: String?,
            mac: Mac
        ): StringBuilder = stringBuilder.appendHashed(mac, input)
    }

    data object ComponentProcessor : HashProcessor<ComponentName> {
        override fun process(
            stringBuilder: StringBuilder,
            input: ComponentName,
            mac: Mac
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
            mac: Mac
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
            mac: Mac
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
            mac: Mac
        ): StringBuilder = stringBuilder.appendHashed(mac, input.name)
    }

    data object ResolveInfoProcessor : HashProcessor<ResolveInfo> {
        override fun process(
            stringBuilder: StringBuilder,
            input: ResolveInfo,
            mac: Mac
        ) = stringBuilder.apply {
            val packageName = packageName(input)
            StringProcessor.process(this, packageName!!, mac)
        }

        private fun packageName(input: ResolveInfo) =
            if (input.activityInfo != null) input.activityInfo.packageName
            else if (input.serviceInfo != null) input.serviceInfo.packageName
            else if (input.providerInfo != null) input.providerInfo.packageName
            else null
    }

    data object ResolveInfoListProcessor : HashProcessor<List<ResolveInfo>> {
        override fun process(
            stringBuilder: StringBuilder,
            input: List<ResolveInfo>,
            mac: Mac
        ): StringBuilder {
            input.forEach {
                ResolveInfoProcessor.process(stringBuilder, it, mac)
            }

            return stringBuilder
        }
    }

    fun process(stringBuilder: StringBuilder, input: T, mac: Mac): StringBuilder
}
