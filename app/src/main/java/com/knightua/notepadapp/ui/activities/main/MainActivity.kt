package com.knightua.notepadapp.ui.activities.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.knightua.basemodule.abstracts.view.BaseCompatActivity
import com.knightua.notepadapp.R
import com.knightua.notepadapp.databinding.ActivityMainBinding
import javax.inject.Inject

class MainActivity : BaseCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mNavController: NavController

    @Inject
    lateinit var presenter: MainActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    @SuppressLint("CheckResult")
    fun init() {
        DaggerMainActivityComponent.create().inject(this)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment)

        presenter.attach(this)
    }
}
