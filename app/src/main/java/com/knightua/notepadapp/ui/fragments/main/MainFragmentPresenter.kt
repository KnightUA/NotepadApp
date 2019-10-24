package com.knightua.notepadapp.ui.fragments.main

import SwipeToDeleteCallback
import android.annotation.SuppressLint
import android.content.IntentFilter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxrelay2.BehaviorRelay
import com.knightua.basemodule.abstracts.presenter.BasePresenter
import com.knightua.notepadapp.R
import com.knightua.notepadapp.adapters.NoteRvAdapter
import com.knightua.notepadapp.di.application.NotepadApp
import com.knightua.notepadapp.receivers.NetworkReceiver
import com.knightua.notepadapp.reposotories.NoteRepository
import com.knightua.notepadapp.room.entity.Note
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber

class MainFragmentPresenter : BasePresenter<MainFragmentView>(),
    NetworkReceiver.NetworkReceiverListener {

    companion object {
        const val STATE_EMPTY_SCREEN = 0

        const val STATE_NO_CONNECTION_DATABASE_EMPTY = 1
        const val STATE_NO_CONNECTION_DATABASE_WITH_DATA = 2
        const val STATE_NO_DATA = 3

        const val STATE_LOADING_DATABASE_EMPTY = 4
        const val STATE_LOADING_DATABASE_WITH_DATA = 5

        const val STATE_DATA_RECEIVED = 6

        const val DATABASE_STATE_EMPTY = 7
        const val DATABASE_STATE_WITH_DATA = 8
    }

    private val stateRelay = BehaviorRelay.createDefault(STATE_EMPTY_SCREEN)

    private lateinit var mNetworkReceiver: NetworkReceiver
    private val mOnItemClickListener: NoteRvAdapter.OnItemClickListener by lazy {
        return@lazy object : NoteRvAdapter.OnItemClickListener {
            override fun onItemClick(item: Note) {
                getView()?.showToast(String.format("Item %s clicked", item.title))
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
                        STATE_LOADING_DATABASE_EMPTY -> {
                            Timber.i("State: STATE_LOADING_DATABASE_EMPTY")
                            getView()?.showLoadingCircle(true)

                        }
                        STATE_LOADING_DATABASE_WITH_DATA -> {
                            Timber.i("State: STATE_LOADING_DATABASE_WITH_DATA")
                            getView()?.showData()
                            getView()?.showLoadingHorizontal(true)
                        }
                        STATE_DATA_RECEIVED -> {
                            Timber.i("State: STATE_DATA_RECEIVED")
                            getView()?.showData()
                        }
                        STATE_NO_CONNECTION_DATABASE_EMPTY -> {
                            Timber.i("State: STATE_NO_CONNECTION_DATABASE_EMPTY")
                            getView()?.showTextError(R.string.error_no_connection)
                        }
                        STATE_NO_CONNECTION_DATABASE_WITH_DATA -> {
                            Timber.i("State: STATE_NO_CONNECTION_DATABASE_WITH_DATA")
                            getView()?.showData()
                            getView()?.showSnackbarError(R.string.error_no_connection)
                        }
                        STATE_NO_DATA -> {
                            Timber.i("State: STATE_NO_DATA")

                            getView()?.showTextError(R.string.error_no_data)
                        }
                    }
                }
        )
        viewCompositeDisposable.add(
            NotepadApp.injector.getNoteRepository().getObserverForDatabase().subscribe { databaseState ->
                when (databaseState.first) {
                    NoteRepository.DATA_UPDATED -> {
                        Timber.i("Database State: DATA_UPDATED")
                        mAdapter.updateAll(notes = databaseState.second)
                    }
                    NoteRepository.DATA_INSERTED -> {
                        Timber.i("Database State: DATA_INSERTED")
                        mAdapter.addAll(notes = databaseState.second)
                    }
                    NoteRepository.DATA_DELETED -> {
                        Timber.i("Database State: DATA_DELETED")
                        mAdapter.deleteAll(notes = databaseState.second)
                    }
                    NoteRepository.DATA_CLEARED -> {
                        Timber.i("Database State: DATA_CLEARED")
                        mAdapter.clearAll()
                    }
                }
            }
        )
    }

    private fun initState() {
        viewCompositeDisposable.add(
            NotepadApp.injector.getNoteRepository().getAllFromDatabase()
                .subscribe({
                    if (!it.isNullOrEmpty()) {
                        if (NetworkReceiver.isConnected(context()))
                            stateRelay.accept(STATE_LOADING_DATABASE_WITH_DATA)
                        else
                            stateRelay.accept(STATE_NO_CONNECTION_DATABASE_WITH_DATA)
                    } else {
                        if (NetworkReceiver.isConnected(context()))
                            stateRelay.accept(STATE_LOADING_DATABASE_EMPTY)
                        else
                            stateRelay.accept(STATE_NO_CONNECTION_DATABASE_EMPTY)
                    }

                    mAdapter.addAll(it)
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

        if (mAdapter.itemCount > 0) {
            if (isConnected) {
                stateRelay.accept(STATE_LOADING_DATABASE_WITH_DATA)
                loadData()
            } else {
                stateRelay.accept(STATE_NO_CONNECTION_DATABASE_WITH_DATA)
            }
        } else {
            if (isConnected) {
                stateRelay.accept(STATE_LOADING_DATABASE_EMPTY)
                loadData()
            } else {
                stateRelay.accept(STATE_NO_CONNECTION_DATABASE_EMPTY)
            }
        }
    }

    fun addDefaultNote() {
        val defaultNote = Note("Title", "Description", System.currentTimeMillis())
        NotepadApp.injector.getNoteRepository()
            .insertInDatabase(defaultNote)
        mAdapter.add(defaultNote)
    }

    fun clearData() {
        NotepadApp.injector.getNoteRepository()
            .clearDatabase()
    }

    @SuppressLint("CheckResult")
    fun loadData() {
        NotepadApp.injector.getNoteRepository().getAllFromApi()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::handleData, ::handleError)
    }

    private fun handleData(notes: List<Note>) {
        Timber.i(notes.toString())
        stateRelay.accept(STATE_DATA_RECEIVED)
        mAdapter.addAll(notes)
    }

    private fun handleError(throwable: Throwable) {
        Timber.e(throwable)
        stateRelay.accept(STATE_NO_DATA)
    }

    private fun initAdapter() {
        Timber.i("initAdapter")

        val swipeHandler = object : SwipeToDeleteCallback(context()!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                NotepadApp.injector.getNoteRepository().deleteFromDatabase(
                    mAdapter.getItemAt(
                        viewHolder.adapterPosition
                    ).id
                )
                mAdapter.deleteAt(viewHolder.adapterPosition)
                getView()?.showUndoSnackbar(mAdapter::undoDelete)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(getView()?.getRecyclerView())

        getView()?.getRecyclerView()?.adapter = mAdapter
    }
}