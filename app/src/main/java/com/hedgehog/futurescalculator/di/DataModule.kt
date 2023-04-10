package com.hedgehog.futurescalculator.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.SharedPreferencesMigration
import com.hedgehog.futurescalculator.data.*
import com.hedgehog.futurescalculator.data.EncryptionManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.security.KeyStore

@InstallIn(SingletonComponent::class)
@Module
abstract class DataModule {

    companion object {
        @Provides
        fun provideCacheDataStore(
            @ApplicationContext context: Context,
            encryptionManager: EncryptionManager
        ): DataStore<SettingsEntity> {
            return DataStoreFactory.create(
                serializer = SettingsEntitySerializer(encryptionManager),
                produceFile = { context.dataStoreFile(DATA_STORE_FILE_NAME) },
                corruptionHandler = null,
                migrations = listOf(),
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
            )
//            return context.cacheDataStore
        }

        @Provides
        fun provideKeyStore(): KeyStore {
            return KeyStore.getInstance("AndroidKeyStore").apply {
                load(null)
            }
        }
    }

    @Binds
    abstract fun bindCacheDataSource(cacheDataSource: CacheDataSourceImpl): CacheDataSource

    @Binds
    abstract fun bindKeyStoreDataSource(keyStoreDataSource: KeyStoreDataSourceImpl): KeyStoreDataSource
}

private const val DATA_STORE_FILE_NAME = "CacheDataStore"

//private val Context.cacheDataStore by dataStore(
//    fileName = "CacheDataStore",
//    serializer = SettingsEntitySerializer(EncryptionManager())
//)
