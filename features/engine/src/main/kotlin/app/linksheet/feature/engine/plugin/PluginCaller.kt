package app.linksheet.feature.engine.plugin

import android.content.Intent
import android.content.pm.PackageManager
import app.linksheet.feature.engine.core.rule.PreProcessorInput
import app.linksheet.feature.engine.core.rule.RuleInput

class PluginCaller(val context: PackageManager) {
    fun <I : RuleInput> evaluate(input: I) {
        if (input is PreProcessorInput) {

           input.url
        }
//        val contentResolvers = context.queryIntentContentProviders(
//            Intent("de.mm20.launcher2.action.PLUGIN"),
//            PackageManager.GET_META_DATA,
//        )


    }
}
