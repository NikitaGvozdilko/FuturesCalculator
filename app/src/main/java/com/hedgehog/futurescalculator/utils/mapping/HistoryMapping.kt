package com.hedgehog.futurescalculator.utils.mapping

import com.hedgehog.futurescalculator.data.database.model.HistoryEntity
import com.hedgehog.futurescalculator.data.model.PositionEntity
import com.hedgehog.futurescalculator.domain.model.History
import com.hedgehog.futurescalculator.domain.model.Settings
import com.hedgehog.futurescalculator.ui.model.HistoryItemModel
import com.hedgehog.futurescalculator.utils.ProfitLossCalculator

fun HistoryEntity.toDomain() =
    History(
        id, entryPrice, profit, loss, leverage, position.toDomain()
    )

fun History.toEntity() =
    HistoryEntity(
        id = id,
        entryPrice = entryPrice,
        profit = profit,
        loss = loss,
        leverage = leverage,
        position = position.toEntity()
    )

fun Settings.Position.toEntity(): PositionEntity {
    return when (this) {
        Settings.Position.SHORT -> PositionEntity.SHORT
        Settings.Position.LONG -> PositionEntity.LONG
    }
}

fun PositionEntity.toDomain(): Settings.Position {
    return when (this) {
        PositionEntity.SHORT -> Settings.Position.SHORT
        PositionEntity.LONG -> Settings.Position.LONG
    }
}

fun History.toItemModel(): HistoryItemModel {
//    val takeProfit: Float
//    val stopLoss: Float
//    when (position) {
//        Settings.Position.LONG -> {
//            takeProfit =
//                entryPrice + entryPrice * (profit / 100f / leverage)
//            stopLoss =
//                entryPrice - entryPrice * (loss / 100f / leverage)
//        }
//        Settings.Position.SHORT -> {
//            takeProfit =
//                entryPrice - entryPrice * (profit / 100f / leverage)
//            stopLoss =
//                entryPrice + entryPrice * (loss / 100f / leverage)
//        }
//    }

    val (takeProfit, stopLoss) = ProfitLossCalculator.getProfitLossPair(
        entryPrice,
        profit,
        loss,
        leverage,
        position
    )

    return HistoryItemModel(
        id = id,
        entryPrice = entryPrice,
        profitPercent = profit,
        lossPercent = loss,
        leverage = leverage,
        position = position,
        profit = takeProfit,
        loss = stopLoss
    )
}