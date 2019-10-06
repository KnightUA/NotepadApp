package com.knightua.notepadapp.states.main

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.knightua.notepadapp.ui.fragments.main.MainFragment
import timber.log.Timber

class NormalMainState(val mainFragment: MainFragment) :
    BaseMainState {

    override fun showError(id: Int) {
        Timber.i("showError")

        Snackbar.make(mainFragment.mBinding.root, id, Snackbar.LENGTH_LONG).show()
    }

    override fun showError(errorText: String) {
        Timber.i("showError %s", errorText)

        Snackbar.make(mainFragment.mBinding.root, errorText, Snackbar.LENGTH_LONG).show()
    }

    override fun hideError() {
        Timber.i("hideError")
    }

    override fun showProgress() {
        Timber.i("showProgress")

        mainFragment.mBinding.progressBarHorizontal?.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        Timber.i("hideProgress")

        mainFragment.mBinding.progressBarHorizontal?.visibility = View.GONE
    }
}