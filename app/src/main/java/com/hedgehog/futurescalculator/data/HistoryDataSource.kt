package com.hedgehog.futurescalculator.data

import com.hedgehog.futurescalculator.data.database.dao.HistoryDao
import com.hedgehog.futurescalculator.domain.model.History
import com.hedgehog.futurescalculator.utils.mapping.toDomain
import com.hedgehog.futurescalculator.utils.mapping.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface HistoryDataSource {
    suspend fun getHistory(): Flow<List<History>>
    suspend fun addHistory(history: History)
    suspend fun clearHistory()
}

class HistoryDataSourceImpl @Inject constructor(
    private val historyDao: HistoryDao
) : HistoryDataSource {
    override suspend fun getHistory(): Flow<List<History>> {
        return historyDao.getHistory().map { list ->
            list.map {
                it.toDomain()
            }

        }
    }

    override suspend fun addHistory(history: History) {
        historyDao.addHistory(history.toEntity())
    }

    override suspend fun clearHistory() {
        historyDao.clear()
    }
}