package com.bridgebase.bridgebase

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Base application class for BridgeBase.
 *
 * Responsible for initializing global dependencies, including Hilt DI
 * and Timber logging. Provides convenient access to the application
 * context throughout the app via [applicationContext].
 *
 * Annotated with [HiltAndroidApp] to trigger dependency-injection setup.
 */
@HiltAndroidApp
class App: Application() {

    init {
        // Store singleton instance for static context retrieval
        instance = this
    }

    companion object {
        private var instance: App? = null

        /**
         * Returns the global application context for use in
         * components that lack direct access (e.g., repositories).
         */
        fun applicationContext(): Context {
            return instance!!.applicationContext
        }

    }
    override fun onCreate() {
        super.onCreate()

        // Enable detailed logging in debug builds
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

    }

}
