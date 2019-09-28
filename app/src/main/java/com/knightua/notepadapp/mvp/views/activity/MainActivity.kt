package com.knightua.notepadapp.mvp.views.activity

import android.os.Bundle
import com.knightua.basemodule.abstracts.view.BaseCompatActivity
import com.knightua.notepadapp.R
import com.knightua.notepadapp.di.application.NotepadMvpApp
import com.knightua.notepadapp.mvp.contracts.MainActivityContract
import com.knightua.notepadapp.mvp.presenters.MainActivityPresenter
import javax.inject.Inject

class MainActivity : BaseCompatActivity(), MainActivityContract.View {

    @Inject
    lateinit var presenter: MainActivityPresenter

    override fun init(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        NotepadMvpApp.get().injector.inject(this)
        presenter.attach(this)
    }
}
