package com.hedgehog.futurescalculator.domain.usecase

import com.hedgehog.futurescalculator.data.CacheDataSource
import com.hedgehog.futurescalculator.domain.model.Settings
import javax.inject.Inject

interface SettingsUseCase {
    suspend fun getSettings(): Settings
    suspend fun saveSettings(settings: Settings)
}

class SettingsUseCaseImpl @Inject constructor(private val cacheDataSource: CacheDataSource): SettingsUseCase {

    override suspend fun getSettings(): Settings {
        return cacheDataSource.getSettings()
    }

    override suspend fun saveSettings(settings: Settings) {
        cacheDataSource.saveSettings(settings)
    }
}