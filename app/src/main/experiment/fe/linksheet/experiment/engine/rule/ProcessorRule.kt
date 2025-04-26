package fe.linksheet.experiment.engine.rule

import fe.linksheet.experiment.engine.EngineResult
import fe.std.uri.StdUrl

data class PreProcessorInput(val url: StdUrl) : RuleInput
data class PostProcessorInput(val resultUrl: StdUrl, val originalUrl: StdUrl) : RuleInput

interface PreprocessorRule : Rule<PreProcessorInput, EngineResult>
interface PostprocessorRule : Rule<PostProcessorInput, EngineResult>
