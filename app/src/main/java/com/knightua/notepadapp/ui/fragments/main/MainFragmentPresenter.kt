package com.knightua.notepadapp.ui.fragments.main

import SwipeToDeleteCallback
import android.annotation.SuppressLint
import android.content.IntentFilter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxrelay2.BehaviorRelay
import com.knightua.basemodule.abstracts.presenter.BasePresenter
import com.knightua.notepadapp.R
import com.knightua.notepadapp.adapters.NoteRvAdapter
import com.knightua.notepadapp.di.application.NotepadApp
import com.knightua.notepadapp.receivers.NetworkReceiver
import com.knightua.notepadapp.room.entity.Note
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import java.util.*

class MainFragmentPresenter : BasePresenter<MainFragmentView>(),
    NetworkReceiver.NetworkReceiverListener {

    companion object {
        val EMPTY_DATA = Pair<Int, List<Note>>(0, emptyList())

        const val STATE_EMPTY_SCREEN = 0

        const val STATE_LOADING = 1
        const val STATE_UPDATING = 3

        const val STATE_NO_CONNECTION = 2
        const val STATE_NO_DATA = 4
    }

    private val stateRelay = BehaviorRelay.createDefault(STATE_EMPTY_SCREEN)
    private val dataRelay = BehaviorRelay.createDefault(EMPTY_DATA)
    private val isInternetRelay = BehaviorRelay.createDefault(true)

    private lateinit var mNetworkReceiver: NetworkReceiver
    private val mOnItemClickListener: NoteRvAdapter.OnItemClickListener by lazy {
        return@lazy object : NoteRvAdapter.OnItemClickListener {
            override fun onItemClick(item: Note) {
                getView()?.openDetailNote(item)
            }
        }
    }

    private val mAdapter: NoteRvAdapter by lazy {
        return@lazy NoteRvAdapter(mOnItemClickListener)
    }

    init {
        initState()
    }

    override fun attach(view: MainFragmentView) {
        super.attach(view)
        initAdapter()
        registerReceivers()
        subscribeState()
    }

    override fun detach() {
        super.detach()
        unregisterReceivers()
    }

    private fun subscribeState() {
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
                            dataRelay.value?.second.let { notes ->
                                if (notes.isNullOrEmpty()) {
                                    getView()?.showLoadingCircle(true)
                                } else {
                                    getView()?.showData()
                                    getView()?.showLoadingHorizontal(true)
                                }
                            }
                        }
                        STATE_UPDATING -> {
                            Timber.i("State: STATE_UPDATING")
                            getView()?.showData()
                        }
                        STATE_NO_CONNECTION -> {
                            Timber.i("State: STATE_NO_CONNECTION")
                            isInternetRelay.value?.let { isInternetConnection ->
                                if (isInternetConnection) {
                                    getView()?.showTextError(R.string.error_no_connection)
                                } else {
                                    getView()?.showData()
                                    getView()?.showSnackbarError(R.string.error_no_connection)
                                }
                            }
                        }
                        STATE_NO_DATA -> {
                            Timber.i("State: STATE_NO_DATA")
                            getView()?.showTextError(R.string.error_no_data)
                        }
                    }
                }
        )
        viewCompositeDisposable.add(
            isInternetRelay
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        false -> {
                            stateRelay.accept(STATE_NO_CONNECTION)
                        }
                    }
                }
        )
    }

    private fun initState() {
        dataCompositeDisposable.add(
            NotepadApp.injector.getNoteRepository().getAllFromDatabase()
                .subscribe({
                    val newPair = Pair<Int, List<Note>>(dataRelay.value?.first?.plus(1)!!, it)
                    dataRelay.accept(newPair)
                }, { Timber.e(it) })
        )

        dataCompositeDisposable.add(
            dataRelay
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.first == 1) {
                        stateRelay.accept(STATE_LOADING)
                        loadData()
                    } else {
                        stateRelay.accept(STATE_UPDATING)
                    }
                    mAdapter.clearAndAddAll(it.second)
                }
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
        isInternetRelay.accept(isConnected)
        if (dataRelay.value?.first == 1) {
            stateRelay.accept(STATE_LOADING)
            loadData()
        }
    }

    fun addDefaultNote() {
        val defaultNote =
            Note(UUID.randomUUID().toString(), "New Note", "", System.currentTimeMillis())
        NotepadApp.injector.getNoteRepository()
            .insertInDatabase(defaultNote)
    }

    fun clearData() {
        NotepadApp.injector.getNoteRepository()
            .clearDatabase()
    }

    @SuppressLint("CheckResult")
    fun loadData() {
        if (isInternetRelay.value!!) {
            NotepadApp.injector.getNoteRepository().getAllFromApi()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::handleData, ::handleError)
        }
    }

    private fun handleData(notes: List<Note>) {
        Timber.i(notes.toString())
        if (isInternetRelay.value!!) {
            NotepadApp.injector.getNoteRepository().insertAllInDatabase(notes)
            dataRelay.accept(Pair(dataRelay.value?.first?.plus(1)!!, notes))
        }
    }

    private fun handleError(throwable: Throwable) {
        Timber.e(throwable)
        stateRelay.accept(STATE_NO_DATA)
    }

    private fun initAdapter() {
        Timber.i("initAdapter")

        val swipeHandler = object : SwipeToDeleteCallback(context()!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                mAdapter.addToUndo(viewHolder.adapterPosition)

                getView()?.showUndoSnackbar(
                    mAdapter::undoDelete,
                    object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            for (recentlyDeletedItem in mAdapter.getRecentlyDeletedItems()) {
                                NotepadApp.injector.getNoteRepository()
                                    .deleteFromDatabase(recentlyDeletedItem.second.uuid)
                            }

                            mAdapter.clearUndo()
                        }
                    })
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(getView()?.getRecyclerView())

        getView()?.getRecyclerView()?.adapter = mAdapter
    }
}