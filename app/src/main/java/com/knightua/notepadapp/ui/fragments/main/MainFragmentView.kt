package com.knightua.notepadapp.ui.fragments.main

import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.knightua.basemodule.abstracts.view.BaseView
import com.knightua.notepadapp.room.entity.Note

interface MainFragmentView : BaseView {
    fun showEmptyScreen()
    fun showSnackbarError(@StringRes stringRes: Int)
    fun showUndoSnackbar(action: () -> Unit, dismissCallback : BaseTransientBottomBar.BaseCallback<Snackbar>)
    fun showTextError(@StringRes stringRes: Int)
    fun showLoadingHorizontal(isShown: Boolean)
    fun showLoadingCircle(isShown: Boolean)
    fun showData()
    fun setData(notes: List<Note>)
    fun showToast(text: String)

    fun openDetailNote(note: Note)

    fun getRecyclerView(): RecyclerView
}