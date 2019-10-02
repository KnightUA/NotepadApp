package com.knightua.notepadapp.ui.activities.main

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        MainActivityModule::class
    ]
)
interface MainActivityComponent {

    fun inject(mainActivity: MainActivity)
}