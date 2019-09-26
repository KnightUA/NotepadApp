package com.knightua.notepadapp.config.di

import com.knightua.notepadapp.components.backstage.di.MainActivityModule
import com.knightua.notepadapp.components.ui.MainActivity
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [
        MainActivityModule::class
    ]
)
@Singleton
interface AppDiComponent {
    fun inject(mainActivity: MainActivity)
}