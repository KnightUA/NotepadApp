package com.knightua.notepadapp.ui.fragments.main

import androidx.annotation.StringRes
import com.knightua.basemodule.abstracts.view.BaseView

interface MainFragmentView : BaseView {
    fun showEmptyScreen()
    fun showSnackbarError(@StringRes stringRes: Int)
    fun showTextError(@StringRes stringRes: Int)
    fun showLoadingHorizontal(isShown: Boolean)
    fun showLoadingCircle(isShown: Boolean)
    fun showData()
}