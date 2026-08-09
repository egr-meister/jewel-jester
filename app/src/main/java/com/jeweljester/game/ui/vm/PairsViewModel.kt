package com.jeweljester.game.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jeweljester.game.audio.SoundManager
import com.jeweljester.game.data.Assets
import com.jeweljester.game.data.GameLogic
import com.jeweljester.game.data.GameRepository
import com.jeweljester.game.data.PairsData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class Card(
    val id: Int,
    val jewelRes: Int,
    val faceUp: Boolean = false,
    val matched: Boolean = false
)

enum class Outcome { PLAYING, WON, LOST }

data class PairsUiState(
    val level: Int,
    val columns: Int,
    val totalPairs: Int,
    val cards: List<Card>,
    val matchedPairs: Int,
    val timeLeft: Int,
    val paused: Boolean,
    val outcome: Outcome
)

class PairsViewModel(
    private val repo: GameRepository,
    private val level: Int
) : ViewModel() {

    private val config = PairsData.byLevel(level)

    private val _ui = MutableStateFlow(initialState())
    val ui: StateFlow<PairsUiState> = _ui.asStateFlow()

    private var firstFlipped: Int? = null
    private var evaluating = false
    private var timerJob: Job? = null

    init {
        startTimer()
    }

    private fun initialState(): PairsUiState {
        val deck = GameLogic.buildDeck(config.pairs, Assets.jewels)
        val cards = deck.mapIndexed { i, res -> Card(id = i, jewelRes = res) }
        return PairsUiState(
            level = level,
            columns = config.columns,
            totalPairs = config.pairs,
            cards = cards,
            matchedPairs = 0,
            timeLeft = config.seconds,
            paused = false,
            outcome = Outcome.PLAYING
        )
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_ui.value.timeLeft > 0 && _ui.value.outcome == Outcome.PLAYING) {
                delay(1000)
                val s = _ui.value
                if (!s.paused && s.outcome == Outcome.PLAYING) {
                    _ui.update { it.copy(timeLeft = (it.timeLeft - 1).coerceAtLeast(0)) }
                    if (_ui.value.timeLeft == 0) finish(Outcome.LOST)
                }
            }
        }
    }

    fun onCardClick(index: Int) {
        val state = _ui.value
        if (state.paused || evaluating || state.outcome != Outcome.PLAYING) return
        val card = state.cards[index]
        if (card.faceUp || card.matched) return

        // Открыть карту
        _ui.update { it.copy(cards = it.cards.mapIndexed { i, c -> if (i == index) c.copy(faceUp = true) else c }) }

        val first = firstFlipped
        if (first == null) {
            firstFlipped = index
        } else {
            evaluating = true
            viewModelScope.launch { evaluatePair(first, index) }
        }
    }

    private suspend fun evaluatePair(a: Int, b: Int) {
        val match = _ui.value.cards[a].jewelRes == _ui.value.cards[b].jewelRes
        if (match) {
            delay(300)
            SoundManager.match()
            _ui.update {
                it.copy(
                    cards = it.cards.mapIndexed { i, c ->
                        if (i == a || i == b) c.copy(matched = true) else c
                    },
                    matchedPairs = it.matchedPairs + 1
                )
            }
            if (_ui.value.matchedPairs >= config.pairs) finish(Outcome.WON)
        } else {
            delay(800)
            _ui.update {
                it.copy(
                    cards = it.cards.mapIndexed { i, c ->
                        if (i == a || i == b) c.copy(faceUp = false) else c
                    }
                )
            }
        }
        firstFlipped = null
        evaluating = false
    }

    private fun finish(outcome: Outcome) {
        if (_ui.value.outcome != Outcome.PLAYING) return
        timerJob?.cancel()
        if (outcome == Outcome.WON) SoundManager.win() else SoundManager.lose()
        _ui.update { it.copy(outcome = outcome) }
        viewModelScope.launch { repo.savePairsResult(level, _ui.value.matchedPairs) }
    }

    fun pause() = _ui.update { it.copy(paused = true) }
    fun resume() = _ui.update { it.copy(paused = false) }

    fun restart() {
        timerJob?.cancel()
        firstFlipped = null
        evaluating = false
        _ui.value = initialState()
        startTimer()
    }

    companion object {
        fun factory(repo: GameRepository, level: Int) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    PairsViewModel(repo, level) as T
            }
    }
}
