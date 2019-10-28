package com.knightua.notepadapp.ui.fragments.main

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MainFragmentModule {

    @Singleton
    @Provides
    fun providesMainFragmentPresenter(): MainFragmentPresenter =
        MainFragmentPresenter()
}