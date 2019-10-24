package com.knightua.notepadapp.ui.fragments.main

import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.knightua.basemodule.abstracts.view.BaseView

interface MainFragmentView : BaseView {
    fun showEmptyScreen()
    fun showSnackbarError(@StringRes stringRes: Int)
    fun showUndoSnackbar(action: () -> Unit)
    fun showTextError(@StringRes stringRes: Int)
    fun showLoadingHorizontal(isShown: Boolean)
    fun showLoadingCircle(isShown: Boolean)
    fun showData()
    fun showToast(text: String)

    fun getRecyclerView(): RecyclerView
}