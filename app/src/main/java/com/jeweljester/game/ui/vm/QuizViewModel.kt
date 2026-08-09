package com.jeweljester.game.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jeweljester.game.data.GameRepository
import com.jeweljester.game.data.Question
import com.jeweljester.game.data.QuizData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Неизменяемое состояние экрана викторины. */
data class QuizUiState(
    val title: String,
    val index: Int,
    val total: Int,
    val score: Int,
    val question: Question,
    val selected: Int?,
    val paused: Boolean,
    val finished: Boolean
)

class QuizViewModel(
    private val repo: GameRepository,
    private val categoryId: String
) : ViewModel() {

    private val category = QuizData.byId(categoryId)

    private val _ui = MutableStateFlow(initialState())
    val ui: StateFlow<QuizUiState> = _ui.asStateFlow()

    private var advanceJob: Job? = null

    private fun initialState() = QuizUiState(
        title = category.title,
        index = 0,
        total = category.questions.size,
        score = 0,
        question = category.questions[0],
        selected = null,
        paused = false,
        finished = false
    )

    fun onAnswer(optionIndex: Int) {
        val state = _ui.value
        if (state.selected != null || state.finished || state.paused) return

        val correct = optionIndex == state.question.correctIndex
        val newScore = if (correct) state.score + 1 else state.score
        _ui.update { it.copy(selected = optionIndex, score = newScore) }

        advanceJob?.cancel()
        advanceJob = viewModelScope.launch {
            delay(750)
            advance()
        }
    }

    private fun advance() {
        val state = _ui.value
        val next = state.index + 1
        if (next >= category.questions.size) {
            _ui.update { it.copy(finished = true) }
            viewModelScope.launch { repo.saveQuizScore(categoryId, state.score) }
        } else {
            _ui.update {
                it.copy(index = next, question = category.questions[next], selected = null)
            }
        }
    }

    fun pause() = _ui.update { it.copy(paused = true) }
    fun resume() = _ui.update { it.copy(paused = false) }

    fun restart() {
        advanceJob?.cancel()
        _ui.value = initialState()
    }

    companion object {
        fun factory(repo: GameRepository, categoryId: String) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    QuizViewModel(repo, categoryId) as T
            }
    }
}
