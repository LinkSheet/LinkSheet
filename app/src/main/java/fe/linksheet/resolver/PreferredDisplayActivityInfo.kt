package fe.linksheet.resolver

import fe.linksheet.extension.separated
import fe.linksheet.extension.wrapped
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.log.LogDumpable
import fe.linksheet.module.log.LogDumpable.Companion.dumpObject
import fe.linksheet.module.log.LogHasher

data class PreferredDisplayActivityInfo(
    val app: PreferredApp,
    val displayActivityInfo: DisplayActivityInfo
) : LogDumpable {
    override fun dump(
        stringBuilder: StringBuilder,
        hasher: LogHasher
    ) = stringBuilder.separated(",") {
        item {
            append("preferredApp=")
            wrapped("{", "}") {
                dumpObject(stringBuilder, hasher, app)
            }
        }

        item {
            append("displayActivityInfo=")
            wrapped("{", "}") {
                dumpObject(stringBuilder, hasher, displayActivityInfo)
            }
        }
    }
}
