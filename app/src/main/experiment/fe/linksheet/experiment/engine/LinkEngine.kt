package fe.linksheet.experiment.engine

import fe.linksheet.experiment.engine.context.DefaultEngineRunContext
import fe.linksheet.experiment.engine.context.EngineRunContext
import fe.linksheet.experiment.engine.fetcher.LinkFetcher
import fe.linksheet.experiment.engine.modifier.ClearURLsLinkModifier
import fe.linksheet.experiment.engine.modifier.EmbedLinkModifier
import fe.linksheet.experiment.engine.modifier.LibRedirectLinkModifier
import fe.linksheet.experiment.engine.resolver.amp2html.Amp2HtmlLinkResolver
import fe.linksheet.experiment.engine.resolver.amp2html.Amp2HtmlLocalSource
import fe.linksheet.experiment.engine.resolver.followredirects.FollowRedirectsLinkResolver
import fe.linksheet.experiment.engine.resolver.followredirects.FollowRedirectsLocalSource
import fe.linksheet.experiment.engine.step.AfterStepRule
import fe.linksheet.experiment.engine.step.BeforeStepRule
import fe.linksheet.experiment.engine.rule.PostProcessorInput
import fe.linksheet.experiment.engine.rule.PostprocessorRule
import fe.linksheet.experiment.engine.rule.PreProcessorInput
import fe.linksheet.experiment.engine.rule.PreprocessorRule
import fe.linksheet.experiment.engine.rule.Rule
import fe.linksheet.experiment.engine.rule.RuleInput
import fe.linksheet.experiment.engine.step.SkipStep
import fe.linksheet.experiment.engine.step.StepEnd
import fe.linksheet.experiment.engine.step.StepRule
import fe.linksheet.experiment.engine.step.StepRuleInput
import fe.linksheet.experiment.engine.step.StepRuleResult
import fe.linksheet.experiment.engine.step.StepStart
import fe.linksheet.experiment.engine.step.EngineStep
import fe.linksheet.experiment.engine.step.EngineStepId
import fe.linksheet.experiment.engine.step.InPlaceStep
import fe.linksheet.experiment.engine.step.StepResult
import fe.linksheet.module.repository.CacheRepository
import fe.linksheet.module.resolver.LibRedirectResolver
import fe.std.uri.StdUrl
import io.ktor.client.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


@Suppress("FunctionName")
fun DefaultLinkEngine(
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    client: HttpClient,
    libRedirectResolver: LibRedirectResolver,
    cacheRepository: CacheRepository,
): LinkEngine {
    val pipeline = LinkEngine(
        steps = listOf(
            EmbedLinkModifier(
                ioDispatcher = ioDispatcher
            ),
            LibRedirectLinkModifier(
                ioDispatcher = ioDispatcher,
                resolver = libRedirectResolver,
                useJsEngine = { false }
            ),
            ClearURLsLinkModifier(ioDispatcher = ioDispatcher),
            FollowRedirectsLinkResolver(
                ioDispatcher = ioDispatcher,
                source = FollowRedirectsLocalSource(client = client),
                cacheRepository = cacheRepository,
                allowDarknets = { false },
                followOnlyKnownTrackers = { true },
                useLocalCache = { true }
            ),
            Amp2HtmlLinkResolver(
                ioDispatcher = ioDispatcher,
                source = Amp2HtmlLocalSource(client = client),
                cacheRepository = cacheRepository,
                useLocalCache = { true }
            )
        ),
        dispatcher = ioDispatcher
    )

    return pipeline
}

class LinkEngine(
    private val steps: List<EngineStep<*>>,
    private val rules: List<Rule<*, *>> = emptyList(),
    private val fetchers: List<LinkFetcher<*>> = emptyList(),
    private val logger: EngineLogger = AndroidEngineLogger("LinkEngine"),
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private val scope = CoroutineScope(dispatcher) + CoroutineName("LinkEngine") + CoroutineExceptionHandler { _, e ->
        println(e)
    }
    private val preProcessorRules = rules.filterIsInstance<PreprocessorRule>()
    private val postProcessorRules = rules.filterIsInstance<PostprocessorRule>()

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
            logger.debug { "Checking rule $rule with input $input" }
            val result = with(rule) { context.checkRule(input) }
            logger.debug { "Rule result is $result" }
            if (result == null) continue
            return result
        }

        return null
    }

    private val _events = MutableStateFlow<StepRuleInput?>(null)
    val events = _events.asStateFlow()

    private fun emitEvent(event: StepRuleInput) {
        logger.debug { "Emitting event $event" }
        _events.tryEmit(event)
    }

    private suspend fun <R : StepResult> processStep(
        context: EngineRunContext,
        step: EngineStep<R>,
        url: StdUrl,
    ): Pair<Boolean, StdUrl> {
        val result = with(step) { context.runStep(url) }
        val hasNewUrl = result != null && result.url != url

        if (!hasNewUrl) return false to url
        return true to result.url
    }

    private suspend fun process(
        context: EngineRunContext,
        url: StdUrl,
        depth: Int = 0,
    ): EngineResult = coroutineScope scope@{
        var mutUrl = url
        for (step in steps) {
            if (!isActive) break
            val stepStart = StepStart(depth, step, mutUrl)
            val beforeStepResult = findStepRule<StepStart<*>, StepRuleResult, BeforeStepRule>(
                context, step.id, stepStart
            )
            if (beforeStepResult is SkipStep) continue

            emitEvent(stepStart)
            val (hasNewUrl, resultUrl) = processStep(context, step, mutUrl)

            val stepEnd = StepEnd(depth, step, url, hasNewUrl, resultUrl)
            val afterStepResult = findStepRule<StepEnd<*>, StepRuleResult, AfterStepRule>(
                context, step.id, stepEnd
            )
            if (afterStepResult is SkipStep) continue

            emitEvent(stepEnd)
            if (!hasNewUrl) continue

            if (step !is InPlaceStep) return@scope process(context, resultUrl, depth + 1)
            mutUrl = resultUrl
        }

        UrlEngineResult(mutUrl)
    }

    private suspend fun process(
        context: EngineRunContext,
        url: StdUrl,
    ): EngineResult = coroutineScope scope@{
        val preResult = processRules(context, preProcessorRules, PreProcessorInput(url))
        if (preResult != null) return@scope preResult

        val result = process(context, url, 0)
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
            logger.debug { "Fetching $fetcher" }
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
