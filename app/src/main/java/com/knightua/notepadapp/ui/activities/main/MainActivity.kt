package com.knightua.notepadapp.ui.activities.main

import android.annotation.SuppressLint
import android.os.Bundle
import com.knightua.basemodule.abstracts.view.BaseCompatActivity
import com.knightua.notepadapp.R
import com.knightua.notepadapp.di.application.NotepadApp
import com.knightua.notepadapp.mvp.contracts.MainActivityContract
import com.knightua.notepadapp.reposotories.NoteRepository
import javax.inject.Inject

class MainActivity : BaseCompatActivity() {

    @Inject
    lateinit var presenter: MainActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    @SuppressLint("CheckResult")
    fun init() {
        setContentView(R.layout.activity_main)

        DaggerMainActivityComponent.builder()
            .mainActivityModule(MainActivityModule())
            .build().inject(this)

        presenter.attach(this)
    }
}
