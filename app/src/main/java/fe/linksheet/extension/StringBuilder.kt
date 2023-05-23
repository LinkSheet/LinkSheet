package fe.linksheet.extension

import fe.linksheet.util.stringbuilder.SeparatedStringBuilder
import fe.linksheet.util.stringbuilder.WrapStringBuilder
import javax.crypto.Mac

fun StringBuilder.separated(
    separator: String,
    builder: SeparatedStringBuilder.() -> Unit
) = SeparatedStringBuilder(separator).build(this, builder)

fun StringBuilder.separated(
    separator: String,
    vararg items: StringBuilder.() -> Unit
) = SeparatedStringBuilder(separator, items = items.toMutableList()).build(this, null)

fun StringBuilder.wrapped(
    wrapWith: String,
    builder: StringBuilder.() -> Unit
) = wrapped(wrapWith, wrapWith, builder)


fun StringBuilder.wrapped(
    wrapStart: String,
    wrapEnd: String,
    builder: StringBuilder.() -> Unit
) = WrapStringBuilder(wrapStart, wrapEnd).build(this, builder)

fun StringBuilder.appendHashed(mac: Mac, string: String) = append(mac.hash(string))

