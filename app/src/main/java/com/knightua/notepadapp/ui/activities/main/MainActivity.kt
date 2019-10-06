package com.knightua.notepadapp.ui.activities.main

import android.annotation.SuppressLint
import android.os.Bundle
import com.knightua.basemodule.abstracts.view.BaseCompatActivity
import com.knightua.notepadapp.R
import com.knightua.notepadapp.ui.fragments.main.MainFragment
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
        DaggerMainActivityComponent.create().inject(this)
        presenter.attach(this)

        supportFragmentManager.beginTransaction()
            .add(R.id.frame_container, MainFragment.newInstance())
            .commit()
    }
}
