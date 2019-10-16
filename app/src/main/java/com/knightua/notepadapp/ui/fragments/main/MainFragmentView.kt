package com.knightua.notepadapp.ui.fragments.main

import com.knightua.basemodule.abstracts.view.BaseView

interface MainFragmentView : BaseView {
    fun showEmptyScreen()
    fun showNoConnection()
    fun showNoData()
    fun showLoadingHorizontal(isShown: Boolean)
    fun showLoadingCircle(isShown: Boolean)
}