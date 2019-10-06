package com.knightua.notepadapp.ui.fragments.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.knightua.basemodule.abstracts.view.BaseFragment
import com.knightua.notepadapp.R
import com.knightua.notepadapp.databinding.FragmentMainBinding
import javax.inject.Inject

class MainFragment : BaseFragment() {

    lateinit var mBinding: FragmentMainBinding

    @Inject
    lateinit var presenter: MainFragmentPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.unregisterReceivers()
    }

    @SuppressLint("CheckResult")
    fun init() {
        DaggerMainFragmentComponent.create().inject(this)
        presenter.attach(this)
        presenter.registerReceivers()
        presenter.initState()
    }

    companion object {

        @JvmStatic
        fun newInstance() = MainFragment()

    }
}