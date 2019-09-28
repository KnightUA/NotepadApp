package com.knightua.notepadapp.di.application

import android.app.Application
import com.knightua.notepadapp.di.component.AppDiComponent
import com.knightua.notepadapp.di.component.DaggerAppDiComponent
import com.knightua.notepadapp.di.modules.MainActivityModule
import com.knightua.notepadapp.di.modules.RoomModule

class NotepadMvpApp : Application() {
    lateinit var injector: AppDiComponent
        private set

    override fun onCreate() {
        super.onCreate()
        injector = DaggerAppDiComponent.builder()
            .mainActivityModule(MainActivityModule())
            .roomModule(RoomModule(INSTANCE as Application))
            .build()
    }

    companion object {
        private var INSTANCE: NotepadMvpApp? = null

        @JvmStatic
        fun get(): NotepadMvpApp = INSTANCE!!
    }
}