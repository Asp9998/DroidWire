package com.aryanspatel.droidwire.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aryanspatel.droidwire.data.repository.ArticleDetailFetchRepository
import com.aryanspatel.droidwire.domain.mapper.toDetailUi
import com.aryanspatel.droidwire.domain.mapper.toUi
import com.aryanspatel.droidwire.domain.model.Article
import com.aryanspatel.droidwire.presentation.models.DetailEvent
import com.aryanspatel.droidwire.presentation.models.DetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repo: ArticleDetailFetchRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val id: String = checkNotNull(savedStateHandle["id"])
    private val contentUrl: String =
        checkNotNull(savedStateHandle["url"])

    private val existing: Flow<Article> =
        repo.observeArticleById(id) // Flow<ArticleEntity?>
            .map { it ?: Article(
                id = id, title = "", source = "", url = "",
                thumbUrl = null, publishedAt = 0L, category = "",
                saved = false, summary = "", contentUrl = contentUrl
            )}
            .distinctUntilChanged()

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<DetailEvent>()
    val events: SharedFlow<DetailEvent> = _events.asSharedFlow()

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved.asStateFlow()

    init {
        viewModelScope.launch {

            val preview = existing.first()
            _uiState.value = DetailUiState(
                loading = true,
                article = preview.toDetailUi()
            )
            runCatching { repo.fetchDetail(contentUrl) }
                .onSuccess { dto ->
                    _uiState.value = DetailUiState(
                        loading = false,
                        article = dto.toUi(existing = existing.first())
                    )
                }
                .onFailure { exception ->
                    _uiState.value = DetailUiState(
                        loading = false,
                        article = preview.toDetailUi(
                            description = "Open source to read more.",
                            images = listOfNotNull(preview.thumbUrl)
                        ),
                        error = exception.message
                    )
                }
        }
    }

    fun openExternal(url: String) {
        viewModelScope.launch {
            _events.emit(DetailEvent.OpenExternal(url))
        }
    }

    fun onShare(url: String) {
        viewModelScope.launch {
            _events.emit(DetailEvent.OpenShareSheet(url))
        }
    }
}