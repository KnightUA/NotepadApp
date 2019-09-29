package com.knightua.notepadapp.di.component

import com.knightua.notepadapp.di.modules.PresenterModule
import com.knightua.notepadapp.di.modules.RoomModule
import com.knightua.notepadapp.mvp.views.activity.MainActivity
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [
        PresenterModule::class,
        RoomModule::class
    ]
)
@Singleton
interface AppDiComponent {
    fun inject(mainActivity: MainActivity)
}