package com.knightua.notepadapp.ui.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.knightua.basemodule.abstracts.view.BaseFragment
import com.knightua.notepadapp.R
import com.knightua.notepadapp.databinding.FragmentMainBinding
import javax.inject.Inject

class MainFragment : BaseFragment(), MainFragmentView, View.OnClickListener {

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
        presenter.detach()
    }

    override fun onClick(p0: View?) {

        when (p0?.id) {
            R.id.floating_action_button_add_note -> {
                presenter.addDefaultNote()
            }
        }
    }

    override fun showEmptyScreen() {
        showView(mBinding.textViewError, false)
        showView(mBinding.progressBar, false)
        showView(mBinding.progressBarHorizontal, false)
        showView(mBinding.floatingActionButtonAddNote, false)
    }

    override fun showNoData() {
        mBinding.textViewError.text = getString(R.string.error_no_data)
        showView(mBinding.textViewError, true)
    }

    override fun showLoadingHorizontal(isShown: Boolean) {
        showView(mBinding.progressBarHorizontal, isShown)
    }

    override fun showLoadingCircle(isShown: Boolean) {
        showView(mBinding.progressBar, isShown)
    }

    override fun showNoConnection() {
        mBinding.textViewError.text = getString(R.string.error_no_connection)
        showView(mBinding.textViewError, true)
    }

    private fun init() {
        DaggerMainFragmentComponent.create().inject(this)
        presenter.attach(this)
    }

    companion object {

        @JvmStatic
        fun newInstance() = MainFragment()

    }
}