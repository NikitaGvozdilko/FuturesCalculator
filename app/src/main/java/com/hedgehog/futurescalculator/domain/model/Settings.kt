package com.hedgehog.futurescalculator.domain.model

class Settings(
    val entryPrice: Float,
    val profit: Int,
    val loss: Int,
    val leverage: Int,
    val position: Position
) {
    enum class Position {
        SHORT, LONG
    }
}