package com.knightua.notepadapp.di.modules

import com.knightua.notepadapp.mvp.presenters.MainActivityPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MainActivityModule {

    @Singleton
    @Provides
    fun providesPresenter(): MainActivityPresenter =
        MainActivityPresenter()
}