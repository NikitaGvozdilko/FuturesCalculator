package com.hedgehog.futurescalculator.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hedgehog.futurescalculator.data.model.PositionEntity

@Entity(tableName = "history")
class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val entryPrice: Float,
    val profit: Int,
    val loss: Int,
    val leverage: Int,
    val position: PositionEntity
)