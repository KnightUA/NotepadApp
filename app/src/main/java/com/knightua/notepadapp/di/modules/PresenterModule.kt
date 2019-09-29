package com.knightua.notepadapp.di.modules

import com.knightua.notepadapp.mvp.presenters.MainActivityPresenter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PresenterModule {

    @Singleton
    @Provides
    fun provideMainActivityPresenter(): MainActivityPresenter = MainActivityPresenter()

}