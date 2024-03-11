package fe.linksheet.util.compat

import android.net.Uri
import fe.linksheet.util.AndroidVersion
import java.net.URI

@JvmInline
value class CompatUriHost(val value: String)

inline val Uri.compatHost: CompatUriHost?
    get() = (if (AndroidVersion.AT_LEAST_API_29_Q) host else URI(this.toString()).host)?.let { CompatUriHost(it) }

