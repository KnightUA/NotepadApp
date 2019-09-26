package com.knightua.basemodule.abstracts.presenter

import com.knightua.basemodule.abstracts.view.BaseView

interface BaseMvpPresenter<V : BaseView> {
    var isAttached: Boolean
    fun attach(view: V)
    fun detach()
}