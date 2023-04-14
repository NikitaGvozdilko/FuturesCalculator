package com.hedgehog.futurescalculator.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.room.Room
import com.hedgehog.futurescalculator.data.*
import com.hedgehog.futurescalculator.data.EncryptionManager
import com.hedgehog.futurescalculator.data.database.FuturesDatabase
import com.hedgehog.futurescalculator.data.database.dao.HistoryDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
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

        @Provides
        fun provideDataSource(@ApplicationContext context: Context): FuturesDatabase {
            val factory = SupportFactory(SQLiteDatabase.getBytes("PassPhrase".toCharArray()))
            return Room.databaseBuilder(context, FuturesDatabase::class.java, "FuturesDataSource")
                .openHelperFactory(factory)
                .build()
        }

        @Provides
        fun provideHistoryDao(futuresDatabase: FuturesDatabase): HistoryDao {
            return futuresDatabase.historyDao
        }
    }

    @Binds
    abstract fun bindCacheDataSource(cacheDataSource: CacheDataSourceImpl): CacheDataSource

    @Binds
    abstract fun bindKeyStoreDataSource(keyStoreDataSource: KeyStoreDataSourceImpl): KeyStoreDataSource

    @Binds
    abstract fun bindHistoryDataSource(historyDataSource: HistoryDataSourceImpl): HistoryDataSource
}

private const val DATA_STORE_FILE_NAME = "CacheDataStore"

//private val Context.cacheDataStore by dataStore(
//    fileName = "CacheDataStore",
//    serializer = SettingsEntitySerializer(EncryptionManager())
//)
