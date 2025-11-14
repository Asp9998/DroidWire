package com.aryanspatel.droidwire.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.aryanspatel.droidwire.domain.mapper.toUi
import com.aryanspatel.droidwire.domain.model.Article
import com.aryanspatel.droidwire.domain.model.Category
import com.aryanspatel.droidwire.domain.repo.ArticleRepository
import com.aryanspatel.droidwire.presentation.models.ArticleUi
import com.aryanspatel.droidwire.presentation.models.HomeEvent
import com.aryanspatel.droidwire.presentation.models.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel @Inject constructor(
    private val repo: ArticleRepository
) : ViewModel(){

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    val feed: Flow<PagingData<ArticleUi>> =
        state
            .map { it.selected }                 // only depend on category for now
            .distinctUntilChanged()
            .flatMapLatest { cat ->
                repo.paged(cat.key)              // Flow<PagingData<Article>>
                    .map { paging -> paging.map { it.toUi() } }  // → ArticleUi
            }
            .cachedIn(viewModelScope)

    private val _events = MutableSharedFlow<HomeEvent>(
        replay = 0,                // do not re-emit on re-subscribe
        extraBufferCapacity = 1,   // drop oldest when bursty
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<HomeEvent> = _events

//    private val allCountFlow: Flow<Int> =
//        articleDao.countAll()                        // @Query("SELECT COUNT(*) FROM articles")
//    private fun catCountFlow(cat: Category): Flow<Int> =
//        articleDao.countByCategory(cat.key)         // @Query("SELECT COUNT(*) FROM articles WHERE category=:category")

    init{
        // keep counts in state
        viewModelScope.launch {
//            allCountFlow.collect { total ->
//                _state.update { it.copy(totalCount = total) }
            }


        viewModelScope.launch {
            // re-collect when category changes
//            state.map { it.selected }.distinctUntilChanged()
//                .flatMapLatest { cat -> catCountFlow(cat) }
//                .collect { count -> _state.update { it.copy(categoryCount = count) } }
        }
    }

    private suspend fun send(event: HomeEvent) { _events.emit(event) }
    fun trySend(event: HomeEvent) { _events.tryEmit(event) }

    // ---------- Intents ----------
    fun selectCategory(cat: Category) {
        if (cat != _state.value.selected) _state.update { it.copy(selected = cat, error = null) }
    }

    fun onQueryChange(q: String) {
        if (q != _state.value.query) _state.update { it.copy(query = q) }
    }

    fun setRefreshing(refreshing: Boolean) {
        _state.update { it.copy(isRefreshing = refreshing) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }


//    val feed: Flow<PagingData<Article>> =
//        repo.paged("android").cachedIn(viewModelScope)

//    suspend fun seedFake(category: String = "android") {
//        val now = System.currentTimeMillis()
//        val items = (1..100).map { i ->
//            ArticleEntity(
//                id = "seed-$category-$i",
//                title = "Article $i",
//                source = "Seed Source",
//                url = "https://example.com/$i",
//                thumbUrl = null,
//                publishedAt = now - i * 60_000L,
//                category = category,
//                saved = false,
//                summary = "",
//                contentUrl = ""
//            )
//        }
//        articleDao.upsertAll(items)
//    }
}