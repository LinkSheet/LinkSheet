package fe.linksheet.module.log

import android.content.ComponentName
import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import android.net.Uri
import fe.linksheet.extension.appendHashed
import fe.stringbuilder.util.curlyWrapped
import fe.stringbuilder.util.slashSeparated
import javax.crypto.Mac

sealed interface LogHasher {
    object NoOpHasher : LogHasher {
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
typealias UrlProcessor = HashProcessor.StringProcessor

sealed interface HashProcessor<T> {
    object NoOpProcessor : HashProcessor<String> {
        override fun process(
            stringBuilder: StringBuilder,
            input: String,
            mac: Mac
        ): StringBuilder = stringBuilder.append(input)
    }

    object UrlProcessor : HashProcessor<String> {
        override fun process(
            stringBuilder: StringBuilder,
            input: String,
            mac: Mac
        ): StringBuilder = stringBuilder.appendHashed(mac, input)
    }

    object UriProcessor : HashProcessor<Uri> {
        override fun process(
            stringBuilder: StringBuilder,
            input: Uri,
            mac: Mac
        ): StringBuilder = UrlProcessor.process(stringBuilder, input.toString(), mac)
    }

    object StringProcessor : HashProcessor<String> {
        override fun process(
            stringBuilder: StringBuilder,
            input: String,
            mac: Mac
        ): StringBuilder = stringBuilder.appendHashed(mac, input)
    }

    object ComponentProcessor : HashProcessor<ComponentName> {
        override fun process(
            stringBuilder: StringBuilder,
            input: ComponentName,
            mac: Mac
        ) = stringBuilder.curlyWrapped {
            slashSeparated {
                item { PackageProcessor.process(stringBuilder, input.packageName, mac) }
                item { appendHashed(mac, input.className) }
            }
        }
    }

    object ActivityInfoProcessor : HashProcessor<ActivityInfo> {
        override fun process(
            stringBuilder: StringBuilder,
            input: ActivityInfo,
            mac: Mac
        ): StringBuilder = stringBuilder.appendHashed(mac, input.name)
    }

    object ResolveInfoProcessor : HashProcessor<ResolveInfo> {
        override fun process(
            stringBuilder: StringBuilder,
            input: ResolveInfo,
            mac: Mac
        ) = stringBuilder.apply {
            val packageName = packageName(input)
            PackageProcessor.process(this, packageName!!, mac)
        }

        private fun packageName(input: ResolveInfo) =
            if (input.activityInfo != null) input.activityInfo.packageName
            else if (input.serviceInfo != null) input.serviceInfo.packageName
            else if (input.providerInfo != null) input.providerInfo.packageName
            else null
    }

    fun process(stringBuilder: StringBuilder, input: T, mac: Mac): StringBuilder
}