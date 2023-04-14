package com.hedgehog.futurescalculator.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hedgehog.futurescalculator.data.database.dao.HistoryDao
import com.hedgehog.futurescalculator.data.database.model.HistoryEntity

@Database(
    version = 1,
    entities = [
        HistoryEntity::class
    ]
)
abstract class FuturesDatabase: RoomDatabase() {
    abstract val historyDao: HistoryDao
}