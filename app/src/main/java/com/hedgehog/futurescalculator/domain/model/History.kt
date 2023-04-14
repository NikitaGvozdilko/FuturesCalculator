package com.hedgehog.futurescalculator.domain.model

class History(
    val id: Long,
    val entryPrice: Float,
    val profit: Int,
    val loss: Int,
    val leverage: Int,
    val position: Settings.Position
)