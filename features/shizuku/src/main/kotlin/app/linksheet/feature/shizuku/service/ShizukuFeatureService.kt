package app.linksheet.feature.shizuku.service

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import app.linksheet.api.eventbus.BroadcastEventBus
import app.linksheet.api.eventbus.IntentEventHandler
import app.linksheet.feature.shizuku.IShizukuUserService
import app.linksheet.feature.shizuku.core.ShizukuDomainVerification
import fe.android.lifecycle.LifecycleAwareService
import fe.composekit.core.AndroidVersion
import fe.composekit.log.createLogger
import fe.linksheet.util.IntentFilters
import fe.std.result.tryCatch
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class ShizukuFeatureService(
    private val eventBus: BroadcastEventBus,
    private val enabled: StateFlow<Boolean>,
    private val autoDisableLinkHandlers: StateFlow<Boolean>,
    private val userServiceFlow: StateFlow<IShizukuUserService?>
) : LifecycleAwareService {

    private var commandProcessor: CommandProcessor? = null

    override suspend fun onAppInitialized(owner: LifecycleOwner) {
        if (!AndroidVersion.isAtLeastApi31S()) return

        AutoDisableHandler(eventBus, enabled, autoDisableLinkHandlers, owner.lifecycleScope).init()
        commandProcessor = CommandProcessor(enabled, userServiceFlow, owner.lifecycleScope)
        commandProcessor?.init()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    suspend fun reset(resultHandler: (Int) -> Unit) {
        commandProcessor?.enqueue(
            ShizukuCommand(
                command = { reset("all") },
                resultHandler = resultHandler
            )
        )
        delay(5000.milliseconds)
        commandProcessor?.enqueue(
            ShizukuCommand(
                command = { verify(null) },
                resultHandler = resultHandler
            )
        )
    }
}

class AutoDisableHandler(
    private val eventBus: BroadcastEventBus,
    private val enabled: StateFlow<Boolean>,
    private val autoDisableLinkHandlers: StateFlow<Boolean>,
    private val scope: CoroutineScope,
) : IntentEventHandler {
    private val logger = createLogger<AutoDisableHandler>()

    fun init() = scope.launch {
        enabled.combine(autoDisableLinkHandlers) { enabled, autoDisable -> enabled to autoDisable }
            .collect { (enabled, autoDisable) ->
                if (enabled && autoDisable) {
                    eventBus.register(this@AutoDisableHandler)
                } else {
                    eventBus.unregister(this@AutoDisableHandler)
                }
            }
    }

    override val filter = IntentFilters.packageState

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_PACKAGE_ADDED) return
        val packageName = intent.data!!.schemeSpecificPart

        if (!AndroidVersion.isAtLeastApi31S()) return
        logger.debug("Disabling link handling for $packageName")
        // TODO: Handle dead binder etc.
        tryCatch {
            ShizukuDomainVerification.setDomainVerificationLinkHandlingAllowed(
                packageName,
                false
            )
        }

//                Toast.makeText(
//                    context,
//                    "Disabled link handling for $packageName",
//                    Toast.LENGTH_SHORT
//                ).show()
    }
}

class CommandProcessor(
    private val enabled: StateFlow<Boolean>,
    private val userServiceFlow: StateFlow<IShizukuUserService?>,
    private val scope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val logger = createLogger<CommandProcessor>()
    private val commandQueue = Channel<ShizukuCommand<*>>(Channel.UNLIMITED)
    private var job: Job? = null

    fun init() = scope.launch {
        enabled.combine(userServiceFlow) { enabled, userService -> enabled to userService }
            .collect { (enabled, service) ->
                logger.debug("commandProcessor collect($enabled, $service)")
                updateActive(enabled, service)
            }
    }

    suspend fun updateActive(enabled: Boolean, userService: IShizukuUserService?) {
        job?.cancelAndJoin()
        if (enabled && userService != null) {
            job = scope.launch(dispatcher) { process(userService) }
        }
    }

    private suspend fun process(userService: IShizukuUserService) {
        while (currentCoroutineContext().isActive) {
            val cmd = commandQueue.receive()
            logger.debug("received cmd $cmd")
            cmd.execute(userService)
        }
    }

    fun enqueue(cmd: ShizukuCommand<*>) {
        commandQueue.trySend(cmd)
    }
}
