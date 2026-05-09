package fe.linksheet.module

import android.content.ClipboardManager
import android.net.Uri
import app.linksheet.api.preference.AppPreferenceRepository
import fe.composekit.extension.getFirstText
import fe.composekit.preference.asFlow
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.web.UriUtil
import fe.std.coroutines.BaseRefreshableFlow
import fe.std.coroutines.RefreshableStateFlow
import fe.std.coroutines.asStateFlow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class ClipboardUseCase(
    private val repository: AppPreferenceRepository,
    private val clipboardManager: ClipboardManager,
    private val coroutineScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AutoCloseable {
    private val isPermittedFlow = repository.asFlow(AppPreferences.homeClipboardCard)

    private fun readClipboard(): ClipboardState {
        if (!isPermittedFlow.value) {
            return ClipboardState.Disabled
        }

        return clipboardManager.getFirstText()
            ?.let { UriUtil.parseWebUriStrict(it) }
            ?.let { ClipboardState.Content(it) }
            ?: ClipboardState.Empty
    }

    private val listener = ClipboardManager.OnPrimaryClipChangedListener {
        coroutineScope.launch {
            contentFlow.refresh()
        }
    }

    fun init() {
        clipboardManager.addPrimaryClipChangedListener(listener)
        coroutineScope.launch {
            isPermittedFlow.collect {
                contentFlow.refresh()
            }
        }
    }

    private val _contentFlow = RefreshableStateFlow { readClipboard() }
    val contentFlow = _contentFlow.asStateFlow()
//
//    fun getContentFlow(): Flow<ClipboardState> {
//        val contentFlow = TestFlow { readClipboard() }
//        return contentFlow.make(isPermittedFlow)
//    }

    override fun close() {
        clipboardManager.removePrimaryClipChangedListener(listener)
    }
}

fun <T> RefreshableStateFlow<T>.refreshOn(signal: Flow<Boolean>): BaseRefreshableFlow<T> {
    val x = flow {
        signal.collect { refresh() }
        this@refreshOn.collect {
            emit(it)
        }
    }

    return object : BaseRefreshableFlow<T>, Flow<T> by x {
        override suspend fun refresh() {
            this@refreshOn.refresh()
        }
    }
}

sealed interface ClipboardState {
    data object Disabled : ClipboardState
    data object Empty : ClipboardState
    data class Content(val content: Uri) : ClipboardState
}
