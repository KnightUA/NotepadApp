package com.knightua.notepadapp.ui.activities.main

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MainActivityModule {

    @Singleton
    @Provides
    fun providesMainActivityPresenter(): MainActivityPresenter = MainActivityPresenter()
}