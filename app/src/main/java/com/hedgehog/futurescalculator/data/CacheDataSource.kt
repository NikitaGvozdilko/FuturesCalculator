package com.hedgehog.futurescalculator.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.hedgehog.futurescalculator.domain.model.Settings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

interface CacheDataSource {
    suspend fun saveSettings(settings: Settings)
    suspend fun getSettings(): Settings
}

class CacheDataSourceImpl @Inject constructor(private val dataStore: DataStore<SettingsEntity>) :
    CacheDataSource {
    override suspend fun saveSettings(settings: Settings) {
        dataStore.updateData {
            SettingsEntity(
                settings.entryPrice,
                settings.profit,
                settings.loss,
                settings.leverage,
                settings.position
            )
        }
    }

    override suspend fun getSettings(): Settings {
        return dataStore.data.first().let {
            Settings(
                it.entryPrice,
                it.profit,
                it.loss,
                it.leverage,
                it.position
            )
        }
    }
}