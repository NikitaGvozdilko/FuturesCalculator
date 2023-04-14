package com.hedgehog.futurescalculator.domain.usecase

import com.hedgehog.futurescalculator.data.HistoryDataSource
import com.hedgehog.futurescalculator.domain.model.History
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface HistoryUseCase {
    suspend fun getHistory(): Flow<List<History>>
    suspend fun addHistory(history: History)
    suspend fun clearHistory()
}

class HistoryUseCaseImpl @Inject constructor(
    private val historyDataSource: HistoryDataSource
): HistoryUseCase {
    override suspend fun getHistory(): Flow<List<History>> {
        return historyDataSource.getHistory()
    }

    override suspend fun addHistory(history: History) {
        historyDataSource.addHistory(history = history)
    }

    override suspend fun clearHistory() {
        historyDataSource.clearHistory()
    }

}