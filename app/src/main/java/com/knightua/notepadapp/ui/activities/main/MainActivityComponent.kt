package com.knightua.notepadapp.ui.activities.main

import com.knightua.notepadapp.room.module.RoomModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        RoomModule::class,
        MainActivityModule::class
    ]
)
interface MainActivityComponent {

    fun inject(mainActivity: MainActivity)
    fun inject(mainActivityPresenter: MainActivityPresenter)
}