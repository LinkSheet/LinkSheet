package app.linksheet.feature.engine.engine

import app.linksheet.feature.engine.engine.context.DefaultEngineRunContext
import app.linksheet.feature.engine.engine.context.EngineRunContext
import app.linksheet.feature.engine.engine.fetcher.LinkFetcher
import app.linksheet.feature.engine.engine.rule.PostProcessorInput
import app.linksheet.feature.engine.engine.rule.PostProcessorRule
import app.linksheet.feature.engine.engine.rule.PreProcessorInput
import app.linksheet.feature.engine.engine.rule.PreProcessorRule
import app.linksheet.feature.engine.engine.rule.Rule
import app.linksheet.feature.engine.engine.rule.RuleInput
import app.linksheet.feature.engine.engine.step.AfterStepRule
import app.linksheet.feature.engine.engine.step.BeforeStepRule
import app.linksheet.feature.engine.engine.step.EngineStep
import app.linksheet.feature.engine.engine.step.EngineStepId
import app.linksheet.feature.engine.engine.step.InPlaceStep
import app.linksheet.feature.engine.engine.step.SkipStep
import app.linksheet.feature.engine.engine.step.StepEnd
import app.linksheet.feature.engine.engine.step.StepResult
import app.linksheet.feature.engine.engine.step.StepRule
import app.linksheet.feature.engine.engine.step.StepRuleInput
import app.linksheet.feature.engine.engine.step.StepRuleResult
import app.linksheet.feature.engine.engine.step.StepStart
import fe.std.uri.StdUrl
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LinkEngine(
    private val steps: List<EngineStep<*>>,
    private val rules: List<Rule<*, *>> = emptyList(),
    private val fetchers: List<LinkFetcher<*>> = emptyList(),
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
//    private val logger = Logger("LinkEngine")
    private val scope = CoroutineScope(dispatcher) + CoroutineName("LinkEngine") + CoroutineExceptionHandler { _, e ->
        println(e)
    }
    private val preProcessorRules = rules.filterIsInstance<PreProcessorRule>()
    private val postProcessorRules = rules.filterIsInstance<PostProcessorRule>()

    private suspend inline fun <I : StepRuleInput, R : StepRuleResult, reified SR : StepRule<I, R>> findStepRule(
        context: EngineRunContext,
        stepId: EngineStepId,
        input: I,
    ): R? {
        // TODO: If we only ever have BeforeRule/AfterRule, these can be filtered before and stored
        val filteredRules = rules
            .asSequence()
            .filterIsInstance<SR>()
            .filter { stepId in it.steps }
            .asIterable()
        return processRules(context, filteredRules, input)
    }

    private suspend inline fun <I : RuleInput, R : EngineResult, reified SR : Rule<I, R>> processRules(
        context: EngineRunContext,
        filteredRules: Iterable<SR>,
        input: I,
    ): R? {
        for (rule in filteredRules) {
//            logger.debug("Checking rule $rule with input $input")
            val result = with(rule) { context.checkRule(input) }
//            logger.debug("Rule result is $result")
            if (result == null) continue
            return result
        }

        return null
    }

    private val _events = MutableStateFlow<StepRuleInput?>(null)
    val events = _events.asStateFlow()

    private fun emitEvent(event: StepRuleInput) {
//        logger.debug("Emitting event $event")
        _events.tryEmit(event)
    }

    private suspend fun <R : StepResult> runStep(
        context: EngineRunContext,
        step: EngineStep<R>,
        url: StdUrl,
    ): Pair<Boolean, StdUrl> {
        val result = with(step) { context.runStep(url) }
        val hasNewUrl = result != null && result.url != url

        if (!hasNewUrl) return false to url
        return true to result.url
    }

    private suspend fun processSteps(
        context: EngineRunContext,
        url: StdUrl,
        depth: Int = 0,
    ): app.linksheet.feature.engine.engine.EngineResult = coroutineScope scope@{
        var mutUrl = url
        for (step in steps) {
            if (!isActive) break
            if (!step.enabled()) continue
            val stepStart = StepStart(depth, step, mutUrl)
            val beforeStepResult = findStepRule<StepStart<*>, StepRuleResult, BeforeStepRule>(
                context, step.id, stepStart
            )
            if (beforeStepResult is SkipStep) continue

            emitEvent(stepStart)
            val (hasNewUrl, resultUrl) = runStep(context, step, mutUrl)

            val stepEnd = StepEnd(depth, step, url, hasNewUrl, resultUrl)
            val afterStepResult = findStepRule<StepEnd<*>, StepRuleResult, AfterStepRule>(
                context, step.id, stepEnd
            )
            if (afterStepResult is SkipStep) continue

            emitEvent(stepEnd)
            if (!hasNewUrl) continue

            if (step !is InPlaceStep) return@scope processSteps(context, resultUrl, depth + 1)
            mutUrl = resultUrl
        }

        UrlEngineResult(mutUrl)
    }

    private suspend fun process(
        context: EngineRunContext,
        url: StdUrl,
    ): EngineResult = coroutineScope scope@{
        val preResult = processRules(context, preProcessorRules, PreProcessorInput(url)); if (preResult != null) return@scope preResult

        val result = processSteps(context, url, 0)
        val resultUrl = (result as? UrlEngineResult)?.url ?: url

        val postResult = processRules(context, postProcessorRules, PostProcessorInput(resultUrl, url))
        if (postResult != null) return@scope postResult
        result
    }

    private suspend fun fetch(
        context: EngineRunContext,
        resultUrl: StdUrl
    ) = coroutineScope scope@{
        for (fetcher in fetchers) {
            if (!isActive) break
            if (!fetcher.enabled()) continue
//            logger.debug("Fetching $fetcher")
            if (!context.confirm(fetcher.id)) continue
            launch {
                val result = fetcher.fetch(resultUrl)
                context.put(fetcher.id, result)
            }
        }
    }

    suspend fun process(
        url: StdUrl,
        context: EngineRunContext = DefaultEngineRunContext()
    ): ContextualEngineResult = coroutineScope scope@{
        val result = process(context, url)
        if (result is UrlEngineResult) {
            fetch(context, result.url)
        }

        val sealedContext = context.seal()
        sealedContext to result
    }
}
