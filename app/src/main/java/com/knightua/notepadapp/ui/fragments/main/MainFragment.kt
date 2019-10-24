package com.knightua.notepadapp.ui.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.knightua.basemodule.abstracts.view.BaseFragment
import com.knightua.notepadapp.R
import com.knightua.notepadapp.databinding.FragmentMainBinding
import javax.inject.Inject

class MainFragment : BaseFragment(), MainFragmentView, View.OnClickListener {

    private lateinit var mBinding: FragmentMainBinding

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

    private fun init() {
        DaggerMainFragmentComponent.create().inject(this)
        presenter.attach(this)
        mBinding.floatingActionButtonAddNote.setOnClickListener(this)
    }

    override fun showEmptyScreen() {
        showView(mBinding.textViewError, false)
        showView(mBinding.progressBar, false)
        showView(mBinding.progressBarHorizontal, false)
    }

    override fun showSnackbarError(stringRes: Int) {
        Snackbar.make(mBinding.root, stringRes, Snackbar.LENGTH_LONG).show()
    }

    override fun showTextError(stringRes: Int) {
        mBinding.textViewError.text = getString(stringRes)
    }

    override fun showLoadingHorizontal(isShown: Boolean) {
        showView(mBinding.progressBarHorizontal, isShown)
    }

    override fun showLoadingCircle(isShown: Boolean) {
        showView(mBinding.progressBar, isShown)
    }

    override fun showData() {
        showView(mBinding.recyclerViewNotes, true)
    }

    override fun showToast(text: String) {
        Toast.makeText(
            context,
            text,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun showUndoSnackbar(action: () -> Unit) {
        val snackbar = Snackbar.make(
            mBinding.root
            , R.string.snack_bar_text,
            Snackbar.LENGTH_LONG
        )
        snackbar.setAction(R.string.snack_bar_undo, { v -> action() })
        snackbar.show()
    }

    override fun getRecyclerView(): RecyclerView {
        return mBinding.recyclerViewNotes
    }

    companion object {

        @JvmStatic
        fun newInstance() = MainFragment()

    }
}