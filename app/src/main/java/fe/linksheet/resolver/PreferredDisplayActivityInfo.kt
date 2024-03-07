package fe.linksheet.resolver

import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.redactor.ProtectMap
import fe.linksheet.module.redactor.Redactable
import fe.linksheet.module.redactor.Redactor
import fe.stringbuilder.util.commaSeparated
import fe.stringbuilder.util.curlyWrapped

data class PreferredDisplayActivityInfo(
    val app: PreferredApp,
    val displayActivityInfo: DisplayActivityInfo
) : Redactable<PreferredDisplayActivityInfo> {

    override fun process(builder: StringBuilder, redactor: Redactor): StringBuilder {
        return builder.commaSeparated {
//            item {
//                curlyWrapped { redactor.process(builder, app, "preferredApp=") }
//            }
//
//            item {
//                curlyWrapped { redactor.process(builder, displayActivityInfo, "displayActivityInfo=") }
//            }
        }
    }
}
