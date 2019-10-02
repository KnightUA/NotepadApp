package com.knightua.notepadapp.di.application

import android.app.Application
import com.knightua.notepadapp.di.component.AppDiComponent
import com.knightua.notepadapp.di.component.DaggerAppDiComponent
import com.knightua.notepadapp.room.module.RoomModule
import timber.log.Timber

object NotepadApp : Application() {
    lateinit var injector: AppDiComponent
        private set

    override fun onCreate() {
        super.onCreate()

        initDagger()
        initTimber()
    }

    private fun initTimber() {
        Timber.plant(Timber.DebugTree())
    }

    private fun initDagger() {
        injector = DaggerAppDiComponent.builder()
            .roomModule(RoomModule(this as Application))
            .build()
    }
}