package com.hedgehog.futurescalculator.utils

import com.hedgehog.futurescalculator.domain.model.Settings

object ProfitLossCalculator {
    fun getProfitLossPair(
        entryPrice: Float,
        profit: Int,
        loss: Int,
        leverage: Int,
        position: Settings.Position
    ): Pair<Float, Float> {
        val takeProfit: Float
        val stopLoss: Float
        when (position) {
            Settings.Position.LONG -> {
                takeProfit =
                    entryPrice + entryPrice * (profit / 100f / leverage)
                stopLoss =
                    entryPrice - entryPrice * (loss / 100f / leverage)
            }
            Settings.Position.SHORT -> {
                takeProfit =
                    entryPrice - entryPrice * (profit / 100f / leverage)
                stopLoss =
                    entryPrice + entryPrice * (loss / 100f / leverage)
            }
        }
        return Pair(takeProfit, stopLoss)
    }
}