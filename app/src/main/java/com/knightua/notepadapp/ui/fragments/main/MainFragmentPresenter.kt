package com.knightua.notepadapp.ui.fragments.main

import SwipeToDeleteCallback
import android.annotation.SuppressLint
import android.content.IntentFilter
import com.jakewharton.rxrelay2.BehaviorRelay
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.knightua.basemodule.abstracts.presenter.BasePresenter
import com.knightua.notepadapp.R
import com.knightua.notepadapp.adapters.NoteRvAdapter
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
        const val STATE_EMPTY_SCREEN = 0

        const val STATE_NO_CONNECTION = 1
        const val STATE_NO_DATA = 2

        const val STATE_LOADING = 3
        const val STATE_DATA_RECEIVED = 4

        const val DATABASE_STATE_EMPTY = 5
        const val DATABASE_STATE_WITH_DATA = 6
    }

    private val stateRelay = BehaviorRelay.createDefault(STATE_EMPTY_SCREEN)
    private val databaseStateRelay = BehaviorRelay.createDefault(DATABASE_STATE_EMPTY)

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
                    getView()?.showEmptyScreen()
                    when (it) {
                        STATE_EMPTY_SCREEN -> {
                            Timber.i("State: STATE_EMPTY_SCREEN")
                            getView()?.showEmptyScreen()
                        }
                        STATE_LOADING -> {
                            Timber.i("State: STATE_LOADING")

                            when (databaseStateRelay.value) {
                                DATABASE_STATE_EMPTY -> {
                                    getView()?.showEmptyScreen()
                                    getView()?.showLoadingCircle(true)
                                }
                                DATABASE_STATE_WITH_DATA -> {
                                    getView()?.showData()
                                    getView()?.showLoadingHorizontal(true)
                                }
                            }

                        }
                        STATE_DATA_RECEIVED -> {
                            Timber.i("State: STATE_DATA_RECEIVED")
                            getView()?.showEmptyScreen()
                            getView()?.showData()
                        }
                        STATE_NO_CONNECTION -> {
                            Timber.i("State: STATE_NO_CONNECTION")

                            when (databaseStateRelay.value) {
                                DATABASE_STATE_EMPTY -> getView()?.showTextError(R.string.error_no_connection)
                                DATABASE_STATE_WITH_DATA -> getView()?.showSnackbarError(R.string.error_no_connection)
                            }
                        }
                        STATE_NO_DATA -> {
                            Timber.i("State: STATE_NO_DATA")

                            databaseStateRelay.value?.let { databaseState ->
                                if (databaseState == DATABASE_STATE_EMPTY)
                                    getView()?.showTextError(R.string.error_no_data)
                            }
                        }
                    }
                }
        )
    }

    private fun initState() {
        viewCompositeDisposable.add(
            NotepadApp.injector.getNoteRepository().getAllFromDatabase()
                .subscribe({
                    if (!it.isNullOrEmpty())
                        databaseStateRelay.accept(DATABASE_STATE_WITH_DATA)
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

    fun registerReceivers() {
        initNetworkReceiver()
    }

    fun unregisterReceivers() {
        view?.context?.unregisterReceiver(mNetworkReceiver)
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {

        if (isConnected) {
            loadData()
        } else {
            stateRelay.accept(STATE_NO_CONNECTION)
        }
    }

    fun addDefaultNote() {
        val defaultNote = Note("Title", "Description", System.currentTimeMillis())
        NotepadApp.injector.getNoteRepository()
            .insertInDatabase(defaultNote)
        (view?.mBinding?.recyclerViewNotes?.adapter as NoteRvAdapter).add(defaultNote)
    }

    fun clearData() {
        NotepadApp.injector.getNoteRepository()
            .clearDatabase()
    }

    @SuppressLint("CheckResult")
    fun loadData() {
        NotepadApp.injector.getNoteRepository().getAllFromApi()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                stateRelay.accept(STATE_LOADING)
            }
            .subscribe(::handleData, ::handleError)
    }

    private fun handleData(notes: List<Note>) {
        Timber.i(notes.toString())
        stateRelay.accept(STATE_DATA_RECEIVED)
    }

    private fun handleError(throwable: Throwable) {
        Timber.e(throwable)
        stateRelay.accept(STATE_NO_DATA)
    }

    private fun initAdapter(notes: List<Note>) {
        Timber.i("initAdapter")

        view?.mBinding?.recyclerViewNotes?.adapter = NoteRvAdapter(notes, onItemNoteClickListener)

        val swipeHandler = object : SwipeToDeleteCallback(view?.context!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                NotepadApp.injector.getNoteRepository().deleteFromDatabase(
                    (view?.mBinding?.recyclerViewNotes?.adapter as NoteRvAdapter).getItemAt(
                        viewHolder.adapterPosition
                    ).id
                )
                (view?.mBinding?.recyclerViewNotes?.adapter as NoteRvAdapter).deleteAt(viewHolder.adapterPosition)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(view?.mBinding?.recyclerViewNotes)
    }
}