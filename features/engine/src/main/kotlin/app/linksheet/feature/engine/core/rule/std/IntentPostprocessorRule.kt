package app.linksheet.feature.engine.core.rule.std

import android.content.ComponentName
import app.linksheet.feature.engine.core.EngineResult
import app.linksheet.feature.engine.core.IntentEngineResult
import app.linksheet.feature.engine.core.context.EngineRunContext
import app.linksheet.feature.engine.core.rule.PostProcessorInput
import app.linksheet.feature.engine.core.rule.PostProcessorRule
import fe.composekit.intent.buildIntent
import fe.linksheet.extension.toAndroidUri
import fe.std.uri.StdUrl

class IntentPostprocessorRule(
    private val matcher: UrlMatcher,
    private val definition: IntentRuleDefinition,
) : PostProcessorRule {
    override suspend fun EngineRunContext.checkRule(input: PostProcessorInput): EngineResult? {
        return when {
            !matcher.matches(input.resultUrl) -> empty()
            else -> IntentEngineResult(
                intent = buildIntent(definition.action, input.resultUrl.toAndroidUri()) {
                    if (definition.cls != null) component = ComponentName(definition.packageName, definition.cls)
                    else `package` = definition.packageName
                }
            )
        }
    }
}

// TODO:
// * Implement/conceptualize some sort of matching DSL/API/Serialization schema providing users with matching capabilities
// * Implement logic to evaluate matcher
// * Expose API to user

class IntentRuleDefinition(
    val packageName: String,
    val cls: String?,
    val action: String,
    // TODO: Flags? Other intent extras?
)

interface UrlMatcher {
    fun matches(url: StdUrl): Boolean
}

class RegexUrlMatcher(val regex: Regex) : UrlMatcher {
    override fun matches(url: StdUrl): Boolean {
        val result = regex.matchEntire(url.toString())
        return result != null
    }
}
/*
Intent rule
{
  "$match": {
    "resultUrl": [

    ]
  }
}
 */
