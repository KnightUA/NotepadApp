package com.knightua.basemodule.abstracts.presenter

import com.knightua.basemodule.abstracts.view.BaseView

open class BasePresenter<V : BaseView> : BaseMvpPresenter<V> {
    protected var view: V? = null
        private set

    override var isAttached = view != null

    override fun attach(view: V) {
        this.view = view
    }

    override fun detach() {
        this.view = null
    }
}