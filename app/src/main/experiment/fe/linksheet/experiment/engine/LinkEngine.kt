package fe.linksheet.experiment.engine

import android.content.Intent
import fe.linksheet.experiment.engine.modifier.ClearURLsLinkModifier
import fe.linksheet.experiment.engine.modifier.EmbedLinkModifier
import fe.linksheet.experiment.engine.modifier.LibRedirectLinkModifier
import fe.linksheet.experiment.engine.resolver.amp2html.Amp2HtmlLinkResolver
import fe.linksheet.experiment.engine.resolver.amp2html.Amp2HtmlLocalSource
import fe.linksheet.experiment.engine.resolver.followredirects.FollowRedirectsLinkResolver
import fe.linksheet.experiment.engine.resolver.followredirects.FollowRedirectsLocalSource
import fe.linksheet.module.repository.CacheRepository
import fe.linksheet.module.resolver.LibRedirectResolver
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
                resolver = libRedirectResolver
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
        )
    )

    return pipeline
}

class LinkEngine(
    val steps: List<EngineStep<*>>,
    val rules: List<Rule<*, *>> = emptyList(),
    val logger: EngineLogger = LogcatEngineLogger("LinkEngine"),
    val dispatcher: CoroutineDispatcher = Dispatchers.IO,
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
        // TODO: Vetoes should allow vetoing only a single step, instead of terminating the entire process

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
            val result = with(context) { rule.checkRule(input) }
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
        url: String,
    ): Pair<Boolean, String> {
//        beforeStepHooks.forEach { it.onBeforeRun(step, url) }
        val result = with(context) { step.runStep(url) }
        val hasNewUrl = result != null && result.url != url

//        afterStepHooks.forEach { it.onAfterRun(step, url, result) }
        if (!hasNewUrl) return false to url

        return true to result.url
    }

    private suspend fun process(
        context: EngineRunContext,
        url: String,
        depth: Int = 0,
    ): EngineResult = coroutineScope scope@{
        var mutUrl = url
        for (step in steps) {
            if (!isActive) break
            val stepStart = StepStart(depth, step, mutUrl)
            val beforeStepResult =
                findStepRule<StepStart<*>, StepRuleResult, BeforeStepRule>(context, step.id, stepStart)
            if (beforeStepResult is SkipStep) continue
//            if (beforeStepResult is Terminate) return@scope TerminateResult(beforeStepResult)

            emitEvent(stepStart)
            val (hasNewUrl, resultUrl) = processStep(context, step, mutUrl)

            val stepEnd = StepEnd(depth, step, url, hasNewUrl, resultUrl)
            val afterStepResult = findStepRule<StepEnd<*>, StepRuleResult, AfterStepRule>(context, step.id, stepEnd)
            if (afterStepResult is SkipStep) continue
//            if (afterStepResult is Terminate) return@scope TerminateResult(afterStepResult)

            emitEvent(stepEnd)
            if (!hasNewUrl) continue

            if (step !is InPlaceStep) return@scope process(context, resultUrl, depth + 1)
            mutUrl = resultUrl
        }

        UrlEngineResult(mutUrl)
    }

    suspend fun process(
        url: String,
        context: EngineRunContext = DefaultEngineRunContext()
    ): ContextualEngineResult = coroutineScope scope@{
        val preResult = processRules(context, preProcessorRules, PreProcessorInput(url))
        if (preResult != null) return@scope context to preResult

        val result = process(context, url, 0)
        val resultUrl = (result as? UrlEngineResult)?.url ?: url

        val postResult = processRules(context, postProcessorRules, PostProcessorInput(resultUrl, url))
        if (postResult != null) return@scope context to postResult
        context to result
    }
}

interface EngineRunContext {
    suspend fun <R : StepResult> EngineStep<R>.runStep(url: String): R? {
        return this@EngineRunContext.runStep(url)
    }

    suspend fun <I : RuleInput, R : EngineResult> Rule<I, R>.checkRule(input: I): R? {
        return this@EngineRunContext.checkRule(input)
    }
}

class DefaultEngineRunContext : EngineRunContext {
}


typealias ContextualEngineResult = Pair<EngineRunContext, EngineResult>

interface EngineResult

class IntentEngineResult(val intent: Intent) : EngineResult

class UrlEngineResult(val url: String) : EngineResult


