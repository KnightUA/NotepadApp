package com.knightua.notepadapp.components.ui

import android.os.Bundle
import com.knightua.basemodule.abstracts.view.BaseCompatActivity
import com.knightua.notepadapp.R
import com.knightua.notepadapp.components.backstage.MainActivityContract
import com.knightua.notepadapp.components.backstage.MainActivityPresenter
import com.knightua.notepadapp.config.di.NotepadMvpApp
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
