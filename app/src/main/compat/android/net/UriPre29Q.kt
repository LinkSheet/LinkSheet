package android.net

import fe.composekit.core.AndroidVersion
import java.net.URI

@JvmInline
value class CompatUriHost(val value: String)

inline val Uri.compatHost: CompatUriHost?
    get() = (if (AndroidVersion.isAtLeastApi29Q()) host else URI(this.toString()).host)?.let { CompatUriHost(it) }
