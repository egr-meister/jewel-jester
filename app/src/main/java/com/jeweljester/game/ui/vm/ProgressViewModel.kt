package com.jeweljester.game.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jeweljester.game.data.GameRepository
import com.jeweljester.game.data.model.GameData
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Общий ViewModel для экранов, которые читают прогресс (Levels, Results, Settings). */
class ProgressViewModel(private val repo: GameRepository) : ViewModel() {

    val gameData: StateFlow<GameData> = repo.gameData
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GameData())

    fun resetAll() = viewModelScope.launch { repo.resetAll() }

    fun setSound(enabled: Boolean) = viewModelScope.launch { repo.setSoundEnabled(enabled) }

    companion object {
        fun factory(repo: GameRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ProgressViewModel(repo) as T
        }
    }
}
