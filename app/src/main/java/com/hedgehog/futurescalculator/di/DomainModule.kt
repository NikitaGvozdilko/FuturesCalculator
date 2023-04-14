package com.hedgehog.futurescalculator.di

import com.hedgehog.futurescalculator.domain.usecase.HistoryUseCase
import com.hedgehog.futurescalculator.domain.usecase.HistoryUseCaseImpl
import com.hedgehog.futurescalculator.domain.usecase.SettingsUseCase
import com.hedgehog.futurescalculator.domain.usecase.SettingsUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
abstract class DomainModule {

    @Binds
    abstract fun bindSettingsUseCase(settingsUseCase: SettingsUseCaseImpl): SettingsUseCase

    @Binds
    abstract fun bindHistoryUseCase(historyUseCase: HistoryUseCaseImpl): HistoryUseCase
}