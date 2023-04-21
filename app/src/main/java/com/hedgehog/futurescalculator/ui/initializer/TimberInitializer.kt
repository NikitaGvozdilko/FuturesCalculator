package com.hedgehog.futurescalculator.ui.initializer

import android.content.Context
import androidx.startup.Initializer
import com.hedgehog.futurescalculator.BuildConfig
import timber.log.Timber

class TimberInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList() // list of dependencies should be initialized before current
    }
}