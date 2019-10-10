package com.knightua.notepadapp.ui.fragments.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.knightua.basemodule.abstracts.view.BaseFragment
import com.knightua.notepadapp.R
import com.knightua.notepadapp.adapters.NoteRvAdapter
import com.knightua.notepadapp.databinding.FragmentMainBinding
import javax.inject.Inject

class MainFragment : BaseFragment(), View.OnClickListener {

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

    override fun onClick(p0: View?) {

        when (p0?.id) {
            R.id.floating_action_button_add_note -> {
                presenter.addDefaultNote()
            }
        }
    }

    @SuppressLint("CheckResult")
    fun init() {
        DaggerMainFragmentComponent.create().inject(this)
        presenter.attach(this)
        mBinding.recyclerViewNotes.adapter = NoteRvAdapter()
        presenter.initState()
        mBinding.floatingActionButtonAddNote.setOnClickListener(this)
    }

    companion object {

        @JvmStatic
        fun newInstance() = MainFragment()

    }
}