package com.hedgehog.futurescalculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hedgehog.futurescalculator.domain.model.Settings
import com.hedgehog.futurescalculator.domain.usecase.SettingsUseCase
import com.hedgehog.futurescalculator.data.EncryptionManager
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
                    mode = State.Mode.LONG
                )
            }
        }
    }

    fun updateInput(input: String) {
        encryptionManager.encrypt(input.toByteArray(), null)
        _stateFlow.update { state ->
            createState(
                input = input,
                profitPercent = state.profitPercent,
                riskPercent = state.lossPercent,
                leverage = state.leverage,
                mode = state.mode
            )
        }
    }

    fun updateLossPercent(inputRisk: String) {
        val inputRiskUpdated = if (inputRisk.isEmpty()) {
            0
        } else if (_stateFlow.value.lossPercent == 0) {
            inputRisk.replace("0", "").toInt()
        } else {
            inputRisk.toInt()
        }
        _stateFlow.update { state ->
            createState(
                input = state.inputValue,
                profitPercent = state.profitPercent,
                riskPercent = inputRiskUpdated,
                leverage = state.leverage,
                mode = state.mode
            )
        }
    }

    fun updateProfitPercent(profitPercent: String) {
        val profitPercentUpdated = if (profitPercent.isEmpty()) {
            0
        } else if (_stateFlow.value.profitPercent == 0) {
            profitPercent.replace("0", "").toInt()
        } else {
            profitPercent.toInt()
        }
        _stateFlow.update { state ->
            createState(
                input = state.inputValue,
                profitPercent = profitPercentUpdated,
                riskPercent = state.lossPercent,
                leverage = state.leverage,
                mode = state.mode
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
                mode = state.mode
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
                mode = state.mode
            )
        }
    }

    fun updateMode(mode: State.Mode) {
        _stateFlow.update { state ->
            createState(
                input = state.inputValue,
                profitPercent = state.profitPercent,
                riskPercent = state.lossPercent,
                leverage = state.leverage,
                mode = mode
            )
        }
    }

    private fun createState(
        input: String,
        profitPercent: Int,
        riskPercent: Int,
        leverage: Int,
        mode: State.Mode
    ): State {
        val takeProfit: Float
        val stopLoss: Float
        val inputValue = if (input.isEmpty()) 0f else input.toFloat()
        when (mode) {
            State.Mode.LONG -> {
                takeProfit =
                    inputValue + inputValue * (profitPercent / 100f / leverage)
                stopLoss =
                    inputValue - inputValue * (riskPercent / 100f / leverage)
            }
            State.Mode.SHORT -> {
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
                    leverage = leverage
                )
            )
        }
        return State(input, profitPercent, riskPercent, leverage, stopLoss, takeProfit, mode)
    }

    data class State(
        val inputValue: String,
        val profitPercent: Int,
        val lossPercent: Int,
        val leverage: Int,
        val stopLoss: Float,
        val takeProfit: Float,
        val mode: Mode
    ) {

        enum class Mode {
            LONG, SHORT
        }

        companion object {
            fun default() = State(
                inputValue = "",
                profitPercent = 0,
                lossPercent = 0,
                leverage = 1,
                stopLoss = 0f,
                takeProfit = 0f,
                mode = Mode.LONG
            )
        }
    }
}