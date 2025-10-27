package app.linksheet.feature.engine.core.rule

import app.linksheet.feature.engine.core.EngineResult
import fe.std.uri.StdUrl

data class PreProcessorInput(val url: StdUrl) : RuleInput
data class PostProcessorInput(val resultUrl: StdUrl, val originalUrl: StdUrl) : RuleInput

interface PreProcessorRule : Rule<PreProcessorInput, EngineResult>
interface PostProcessorRule : Rule<PostProcessorInput, EngineResult>
