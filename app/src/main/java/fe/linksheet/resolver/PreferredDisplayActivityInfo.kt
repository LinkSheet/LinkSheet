package fe.linksheet.resolver

import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.log.LogDumpable
import fe.linksheet.module.log.LogDumpable.Companion.dumpObject
import fe.linksheet.module.log.LogHasher
import fe.stringbuilder.util.commaSeparated
import fe.stringbuilder.util.curlyWrapped

data class PreferredDisplayActivityInfo(
    val app: PreferredApp,
    val displayActivityInfo: DisplayActivityInfo
) : LogDumpable {
    override fun dump(
        stringBuilder: StringBuilder,
        hasher: LogHasher
    ) = stringBuilder.commaSeparated {
        item {
            append("preferredApp=")
            curlyWrapped {
                dumpObject(stringBuilder, hasher, app)
            }
        }

        item {
            append("displayActivityInfo=")
            curlyWrapped {
                dumpObject(stringBuilder, hasher, displayActivityInfo)
            }
        }
    }
}
