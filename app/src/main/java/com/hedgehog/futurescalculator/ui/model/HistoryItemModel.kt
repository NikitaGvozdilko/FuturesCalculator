package com.hedgehog.futurescalculator.ui.model

import com.hedgehog.futurescalculator.domain.model.Settings

data class HistoryItemModel(
    val id: Long,
    val entryPrice: Float,
    val profitPercent: Int,
    val lossPercent: Int,
    val leverage: Int,
    val position: Settings.Position,
    val profit: Float,
    val loss: Float
)