package com.knightua.notepadapp.components.backstage.di

import com.knightua.notepadapp.components.backstage.MainActivityPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MainActivityModule {
    @Provides
    @Singleton
    fun providesPresenter(): MainActivityPresenter = MainActivityPresenter()
}