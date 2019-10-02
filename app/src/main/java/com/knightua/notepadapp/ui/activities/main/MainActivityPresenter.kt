package com.knightua.notepadapp.ui.activities.main

import android.app.Application
import com.knightua.basemodule.abstracts.presenter.BasePresenter
import com.knightua.basemodule.abstracts.view.BaseView
import com.knightua.notepadapp.di.application.NotepadApp
import com.knightua.notepadapp.mvp.contracts.MainActivityContract
import com.knightua.notepadapp.reposotories.NoteRepository
import com.knightua.notepadapp.room.module.RoomModule
import javax.inject.Inject

class MainActivityPresenter : BasePresenter<BaseView>() {

    @Inject
    lateinit var noteRepository: NoteRepository

    init {
        NotepadApp.injector.inject(this as BasePresenter<BaseView>)
        DaggerMainActivityComponent.builder()
            .mainActivityModule(MainActivityModule())
            .build().inject(this)
    }
}