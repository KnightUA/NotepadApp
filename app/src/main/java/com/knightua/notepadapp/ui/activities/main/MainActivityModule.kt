package com.knightua.notepadapp.ui.activities.main

import com.knightua.basemodule.abstracts.presenter.BaseMvpPresenter
import com.knightua.notepadapp.room.database.NotepadAppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MainActivityModule {

    @Singleton
    @Provides
    fun providesMainActivityPresenter(): MainActivityPresenter = MainActivityPresenter()

    @Singleton
    @Provides
    fun providesMainActivityView() : MainActivity = MainActivity()
}