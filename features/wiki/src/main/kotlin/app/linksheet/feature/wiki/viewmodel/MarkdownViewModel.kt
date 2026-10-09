package app.linksheet.feature.wiki.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import app.linksheet.feature.wiki.navigation.MarkdownViewerRoute
import app.linksheet.feature.wiki.usecase.WikiArticleUseCase
import fe.std.result.isSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MarkdownViewModel internal constructor(
    private val handle: SavedStateHandle,
    private val useCase: WikiArticleUseCase
) : ViewModel() {
    val data = handle.toRoute<MarkdownViewerRoute>()
    private val _markdownText = MutableStateFlow<String?>(null)
    val markdownText = _markdownText.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    fun init() = viewModelScope.launch {
        if (_isLoading.value) return@launch
        _isLoading.emit(true)
        val textResult = useCase.getWikiText(data.rawUrl, false)
        if (textResult.isSuccess()) {
            _markdownText.emit(textResult.value)
        }
        _isLoading.emit(false)
    }

    fun refresh() = viewModelScope.launch {
        if (_isLoading.value || _isRefreshing.value) return@launch
        _isRefreshing.emit(true)
        val textResult = useCase.getWikiText(data.rawUrl, true)
        if (textResult.isSuccess()) {
            _markdownText.emit(textResult.value)
        }
        _isRefreshing.emit(false)
    }
}
