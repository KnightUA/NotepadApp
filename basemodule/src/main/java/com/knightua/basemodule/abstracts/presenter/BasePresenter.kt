package com.knightua.basemodule.abstracts.presenter

import android.content.Context
import com.knightua.basemodule.abstracts.view.BaseView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.lang.ref.WeakReference

abstract class BasePresenter<V : BaseView> : BaseMvpPresenter<V> {

    protected var view: WeakReference<V>? = null
        private set

    protected val viewCompositeDisposable = CompositeDisposable()
    protected val dataCompositeDisposable = CompositeDisposable()

    override var isAttached = view != null

    override fun attach(view: V) {
        this.view = WeakReference(view)
    }

    override fun detach() {
        this.view = null
        viewCompositeDisposable.clear()
    }

    override fun getView(): V? {
        return view?.get()
    }

    override fun destroyPresenter() {
        dataCompositeDisposable.dispose()
    }

    protected fun <T> addMain(relay: Observable<T>, function: (T) -> Unit) {
        viewCompositeDisposable.add(
            relay.observeOn(AndroidSchedulers.mainThread())
                .subscribe({ function(it) }, { Timber.e(it) })
        )
    }

    fun context(): Context? {
        return getView()?.getContext()
    }
}