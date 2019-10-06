package com.knightua.notepadapp.states.main

import androidx.annotation.StringRes

interface BaseMainState {
    fun showError(@StringRes id: Int)
    fun showError(errorText: String)
    fun hideError()
    fun showProgress()
    fun hideProgress()
}