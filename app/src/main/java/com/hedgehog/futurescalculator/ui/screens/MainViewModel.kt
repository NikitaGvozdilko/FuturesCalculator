package com.hedgehog.futurescalculator.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hedgehog.futurescalculator.domain.model.Settings
import com.hedgehog.futurescalculator.domain.usecase.SettingsUseCase
import com.hedgehog.futurescalculator.data.EncryptionManager
import com.hedgehog.futurescalculator.domain.model.History
import com.hedgehog.futurescalculator.domain.usecase.HistoryUseCase
import com.hedgehog.futurescalculator.ui.model.HistoryItemModel
import com.hedgehog.futurescalculator.utils.mapping.toItemModel
import com.hedgehog.futurescalculator.utils.round
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.Integer.max
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
    private val historyUseCase: HistoryUseCase,
    private val encryptionManager: EncryptionManager
) :
    ViewModel() {
    private val _stateFlow = MutableStateFlow(State.default())
    val stateFlow: Flow<State> = _stateFlow.asStateFlow()

    private var saveSettingsJob: Job? = null

    init {
        viewModelScope.launch {
            settingsUseCase.getSettings().let {
                _stateFlow.value = createState(
                    input = it.entryPrice.toString(),
                    profitPercent = it.profit,
                    riskPercent = it.loss,
                    leverage = it.leverage,
                    position = it.position
                )
            }
        }

        viewModelScope.launch {
            historyUseCase.getHistory().collect {
                _stateFlow.update { state ->
                    state.copy(historyList = it.map {
                        it.toItemModel()
                    })
                }
            }
        }
    }

    fun updateInput(input: String) {
//        encryptionManager.encrypt(input.toByteArray(), null)
        _stateFlow.update { state ->
            createState(
                input = input,
                profitPercent = state.profitPercent,
                riskPercent = state.lossPercent,
                leverage = state.leverage,
                position = state.position
            )
        }
    }

    fun updateLossPercent(inputRisk: String) {
        val inputRiskUpdated = formatProfitLoss(inputRisk, _stateFlow.value.lossPercent)
        _stateFlow.update { state ->
            createState(
                input = state.inputValue,
                profitPercent = state.profitPercent,
                riskPercent = inputRiskUpdated,
                leverage = state.leverage,
                position = state.position
            )
        }
    }

    fun updateProfitPercent(profitPercent: String) {
        val profitPercentUpdated = formatProfitLoss(profitPercent, _stateFlow.value.profitPercent)
        _stateFlow.update { state ->
            createState(
                input = state.inputValue,
                profitPercent = profitPercentUpdated,
                riskPercent = state.lossPercent,
                leverage = state.leverage,
                position = state.position
            )
        }
    }

    fun onAddLeverageClicked() {
        val leverage = _stateFlow.value.leverage + 1
        _stateFlow.update { state ->
            createState(
                input = state.inputValue,
                profitPercent = state.profitPercent,
                riskPercent = state.lossPercent,
                leverage = leverage,
                position = state.position
            )
        }
    }

    fun onMinusLeverageClicked() {
        val leverage = max(_stateFlow.value.leverage - 1, 1)
        _stateFlow.update { state ->
            createState(
                input = state.inputValue,
                profitPercent = state.profitPercent,
                riskPercent = state.lossPercent,
                leverage = leverage,
                position = state.position
            )
        }
    }

    fun updatePosition(position: Settings.Position) {
        _stateFlow.update { state ->
            createState(
                input = state.inputValue,
                profitPercent = state.profitPercent,
                riskPercent = state.lossPercent,
                leverage = state.leverage,
                position = position
            )
        }
    }

    fun onSaveClicked() {
        viewModelScope.launch {
            _stateFlow.value.also { state ->
                historyUseCase.addHistory(
                    History(
                        id = 0,
                        entryPrice = state.inputValue.toFloat(),
                        profit = state.profitPercent,
                        loss = state.lossPercent,
                        leverage = state.leverage,
                        position = state.position
                    )
                )
            }
        }
    }

    fun onClearHistoryClicked() {
        historyUseCase.clearHistory()
    }

    private fun createState(
        input: String,
        profitPercent: Int,
        riskPercent: Int,
        leverage: Int,
        position: Settings.Position
    ): State {
        val takeProfit: Float
        val stopLoss: Float
        val inputValue = try {
            input.replace(",", ".").toFloat()
        } catch (e: Exception) {
            0f
        }

        when (position) {
            Settings.Position.LONG -> {
                takeProfit =
                    inputValue + inputValue * (profitPercent / 100f / leverage)
                stopLoss =
                    inputValue - inputValue * (riskPercent / 100f / leverage)
            }
            Settings.Position.SHORT -> {
                takeProfit =
                    inputValue - inputValue * (profitPercent / 100f / leverage)
                stopLoss =
                    inputValue + inputValue * (riskPercent / 100f / leverage)
            }
        }
        saveSettingsJob?.cancel()
        saveSettingsJob = viewModelScope.launch {
            delay(1000)
            settingsUseCase.saveSettings(
                Settings(
                    entryPrice = inputValue,
                    profit = profitPercent,
                    loss = riskPercent,
                    leverage = leverage,
                    position = position
                )
            )
        }
        return State(
            input,
            profitPercent,
            riskPercent,
            leverage,
            stopLoss.round(1),
            takeProfit.round(1),
            position,
            _stateFlow.value.historyList
        )
    }

    private fun formatProfitLoss(value: String, previousValue: Int) = if (value.isEmpty()) {
        0
    } else if (previousValue == 0) {
        try {
            value.replace("0", "").toInt()
        } catch (ex: Exception) {
            0
        }
    } else {
        try {
            value.toInt()
        } catch (ex: Exception) {
            0
        }
    }

    data class State(
        val inputValue: String,
        val profitPercent: Int,
        val lossPercent: Int,
        val leverage: Int,
        val stopLoss: Float,
        val takeProfit: Float,
        val position: Settings.Position,
        val historyList: List<HistoryItemModel>
    ) {

        companion object {
            fun default() = State(
                inputValue = "",
                profitPercent = 0,
                lossPercent = 0,
                leverage = 1,
                stopLoss = 0f,
                takeProfit = 0f,
                position = Settings.Position.LONG,
                historyList = emptyList()
            )
        }
    }
}