package com.knightua.notepadapp.di.application

import android.app.Application
import android.content.Context
import com.knightua.notepadapp.di.component.AppDiComponent
import com.knightua.notepadapp.di.component.DaggerAppDiComponent
import timber.log.Timber

class NotepadApp : Application() {

    companion object {
        lateinit var injector: AppDiComponent
            private set

        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext

        initDagger()
        initTimber()
    }

    private fun initTimber() {
        Timber.plant(Timber.DebugTree())
    }

    private fun initDagger() {
        injector = DaggerAppDiComponent.create()
    }
}