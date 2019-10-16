package com.knightua.notepadapp.ui.fragments.main

import android.annotation.SuppressLint
import android.content.IntentFilter
import com.jakewharton.rxrelay2.BehaviorRelay
import com.knightua.basemodule.abstracts.presenter.BasePresenter
import com.knightua.notepadapp.di.application.NotepadApp
import com.knightua.notepadapp.receivers.NetworkReceiver
import com.knightua.notepadapp.room.entity.Note
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MainFragmentPresenter : BasePresenter<MainFragmentView>(),
    NetworkReceiver.NetworkReceiverListener {

    companion object {
        private const val STATE_EMPTY_NOTES_IN_DATABASE_LOADING = 1
        private const val STATE_LOADING = 2
        private const val STATE_UNRECEIVED_AND_EMPTY_DATA = 3
        private const val STATE_LOADED = 4
        private const val STATE_EMPTY_SCREEN = 5
    }

    private val stateRelay = BehaviorRelay.createDefault(STATE_EMPTY_SCREEN)
    private lateinit var mNetworkReceiver: NetworkReceiver

    init {
        initState()
    }

    override fun attach(view: MainFragmentView) {
        super.attach(view)
        registerReceivers()
        subscribeState()
    }

    override fun detach() {
        super.detach()
        unregisterReceivers()
    }

    private fun subscribeState() {
        viewCompositeDisposable.add(
            Single.timer(500, TimeUnit.MILLISECONDS)
                .subscribe({
                    if (stateRelay.value == STATE_EMPTY_SCREEN) {
                        stateRelay.accept(STATE_LOADING)
                    }
                }, { Timber.e(it) })
        )

        viewCompositeDisposable.add(
            stateRelay
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        STATE_EMPTY_SCREEN -> {
                            Timber.i("State: STATE_EMPTY_SCREEN")
                            getView()?.showEmptyScreen()
                        }
                        STATE_EMPTY_NOTES_IN_DATABASE_LOADING -> {
                            Timber.i("State: STATE_EMPTY_NOTES_IN_DATABASE_LOADING")
                            getView()?.showLoadingCircle(true)
                        }
                        STATE_LOADING -> {
                            Timber.i("State: STATE_LOADING")
                            getView()?.showLoadingHorizontal(true)
                        }
                        STATE_UNRECEIVED_AND_EMPTY_DATA -> {
                            Timber.i("State: STATE_UNRECEIVED_AND_EMPTY_DATA")
                            getView()?.showNoData()
                        }
                        STATE_LOADED -> {
                            Timber.i("State: STATE_LOADED")
                            getView()?.showNoData()
                        }
                    }
                }
        )
    }

    private fun initState() {
        viewCompositeDisposable.add(
            NotepadApp.injector.getNoteRepository().getAll()
                .subscribe({
                    if (it.isNullOrEmpty())
                        stateRelay.accept(STATE_EMPTY_NOTES_IN_DATABASE_LOADING)
                    else
                        stateRelay.accept(STATE_LOADING)
                }, { Timber.e(it) })
        )
    }

    private fun registerReceivers() {
        initNetworkReceiver()
    }

    private fun unregisterReceivers() {
        getView()?.getContext()?.unregisterReceiver(mNetworkReceiver)
    }

    private fun initNetworkReceiver() {
        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        mNetworkReceiver = NetworkReceiver()
        getView()?.getContext()?.registerReceiver(mNetworkReceiver, intentFilter)
        NetworkReceiver.networkReceiverListener = this
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {

        if (isConnected) {
            loadData()
        } else {
            getView()?.showNoConnection()
        }
    }

    fun addDefaultNote() {
        NotepadApp.injector.getNoteRepository()
            .insertInDatabase(Note("Title", "Description", System.currentTimeMillis()))
    }

    @SuppressLint("CheckResult")
    fun loadData() {
        NotepadApp.injector.getNoteRepository().getAll()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnTerminate {
                getView()?.showLoadingCircle(false)
                stateRelay.accept(STATE_LOADED)
            }
            .doOnSubscribe {
                getView()?.showLoadingCircle(true)
            }
            .subscribe(::handleData, ::handleError)
    }

    private fun handleData(notes: List<Note>) {
        Timber.i(notes.toString())
    }

    private fun handleError(throwable: Throwable) {
        Timber.e(throwable)
    }
}