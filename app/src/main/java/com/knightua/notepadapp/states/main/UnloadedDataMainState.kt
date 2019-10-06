package com.knightua.notepadapp.states.main

import android.view.View
import com.knightua.notepadapp.ui.fragments.main.MainFragment
import timber.log.Timber

class UnloadedDataMainState(val mainFragment: MainFragment) :
    BaseMainState {

    override fun showError(id: Int) {
        Timber.i("showError")

        mainFragment.mBinding.textViewError.setText(id)
        mainFragment.mBinding.textViewError.visibility = View.VISIBLE
    }

    override fun showError(errorText: String) {
        Timber.i("showError %s", errorText)

        mainFragment.mBinding.textViewError.text = errorText
        mainFragment.mBinding.textViewError.visibility = View.VISIBLE
    }

    override fun hideError() {
        Timber.i("hideError")

        mainFragment.mBinding.textViewError.visibility = View.GONE
    }

    override fun showProgress() {
        Timber.i("showProgress")

        mainFragment.mBinding.progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        Timber.i("hideProgress")

        mainFragment.mBinding.progressBar.visibility = View.GONE
    }
}