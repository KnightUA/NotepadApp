package com.knightua.notepadapp.di.application

import android.app.Application
import com.knightua.notepadapp.di.component.AppDiComponent
import com.knightua.notepadapp.di.component.DaggerAppDiComponent
import com.knightua.notepadapp.di.modules.PresenterModule
import com.knightua.notepadapp.di.modules.RoomModule
import timber.log.Timber

class NotepadApp : Application() {
    lateinit var injector: AppDiComponent
        private set

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this

        initDagger()
        initTimber()
    }

    private fun initTimber() {
        Timber.plant(Timber.DebugTree())
    }

    private fun initDagger() {
        injector = DaggerAppDiComponent.builder()
            .presenterModule(PresenterModule())
            .roomModule(RoomModule(this as Application))
            .build()
    }

    companion object {
        private var INSTANCE: NotepadApp? = null

        @JvmStatic
        fun get(): NotepadApp = INSTANCE!!
    }
}