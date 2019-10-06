package com.knightua.notepadapp.ui.fragments.main

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        MainFragmentModule::class
    ]
)
interface MainFragmentComponent {

    fun inject(mainFragment: MainFragment)
}
