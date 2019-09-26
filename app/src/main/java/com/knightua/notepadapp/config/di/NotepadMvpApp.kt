package com.knightua.notepadapp.config.di

import android.app.Application
import com.knightua.notepadapp.components.backstage.di.MainActivityModule

class NotepadMvpApp : Application() {
    lateinit var injector: AppDiComponent
        private set

    override fun onCreate() {
        super.onCreate()
        injector = DaggerAppDiComponent.builder()
            .mainActivityModule(MainActivityModule())
            .build()
    }

    companion object {
        private var INSTANCE: NotepadMvpApp? = null
        @JvmStatic
        fun get(): NotepadMvpApp = INSTANCE!!
    }
}