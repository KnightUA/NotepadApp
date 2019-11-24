package com.knightua.notepadapp.ui.fragments.main

import SwipeToDeleteCallback
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
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*

class MainFragmentPresenter : BasePresenter<MainFragmentView>(),
    NetworkReceiver.NetworkReceiverListener {

    companion object {
        //val EMPTY_DATA = Pair<Int, List<Note>>(0, emptyList())

        const val STATE_EMPTY_SCREEN = 0

        const val STATE_LOADING = 1
        const val STATE_UPDATING = 3

        const val STATE_NO_CONNECTION = 2
        const val STATE_NO_DATA = 4
        const val STATE_DATA = 5
    }

    private val stateRelay = BehaviorRelay.createDefault(STATE_EMPTY_SCREEN)
    private val dataRelay = BehaviorRelay.create<List<Note>>()
    private var networkConnected = true

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
        unregisterReceivers()
        super.detach()
    }

    private fun subscribeState() {
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
                        getView()?.showLoadingCircle(true)
                    }
                    STATE_UPDATING -> {
                        Timber.i("State: STATE_UPDATING")
                        getView()?.showLoadingHorizontal(true)
                        getView()?.showData()
                    }
                    STATE_DATA -> {
                        getView()?.showData()
                    }
                    STATE_NO_CONNECTION -> {
                        Timber.i("State: STATE_NO_CONNECTION")
                        getView()?.showTextError(R.string.error_no_connection)
                    }
                    STATE_NO_DATA -> {
                        Timber.i("State: STATE_NO_DATA")
                        getView()?.showTextError(R.string.error_no_data)
                    }
                }
            }.addTo(viewCompositeDisposable)

        dataRelay
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                getView()?.setData(it)
                //REMOVE
                mAdapter.clearAndAddAll(it)
            }.addTo(viewCompositeDisposable)
    }

    private fun initState() {
        NotepadApp.injector.getNoteRepository()
            .getAllFromDatabase().take(1)
            .subscribeOn(Schedulers.io())
            .doOnNext {
                if (it.isEmpty()) {
                    stateRelay.accept(STATE_LOADING)
                } else {
                    stateRelay.accept(STATE_UPDATING)
                }
                dataRelay.accept(it)
            }
            .flatMap {
                NotepadApp.injector.getNoteRepository().getAllFromApi().toFlowable()
                    .doOnError {
                        if (dataRelay.hasValue() && dataRelay.value?.isEmpty() == true) {
                            stateRelay.accept(STATE_NO_CONNECTION)
                        } else {
                            stateRelay.accept(STATE_DATA)
                            getView()?.showSnackbarError(R.string.error_no_connection)
                        }
                    }
            }
            .flatMap {
                NotepadApp.injector.getNoteRepository().getAllFromDatabase().skip(1)
            }
            .onExceptionResumeNext(NotepadApp.injector.getNoteRepository().getAllFromDatabase())
            .subscribe({
                dataRelay.accept(it)
                if (it.isEmpty()) {
                    stateRelay.accept(STATE_NO_DATA)
                } else {
                    stateRelay.accept(STATE_DATA)
                }
            }, {
                Timber.i("sql exception")
            }).addTo(dataCompositeDisposable)
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
        NotepadApp.injector.getNoteRepository().networkConnected(isConnected)
        if (networkConnected == isConnected) {
            return
        }
        networkConnected = isConnected
        if (isConnected && dataRelay.hasValue() && dataRelay.value?.isEmpty() == true) {
            loadData()
        }
    }

    fun addDefaultNote() {
        val defaultNote =
            Note(UUID.randomUUID().toString(), "New Note", "", System.currentTimeMillis())
        NotepadApp.injector.getNoteRepository()
            .insertInDatabase(defaultNote)
    }

    private fun loadData() {
        NotepadApp.injector.getNoteRepository().getAllFromApi()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                if (dataRelay.value?.isEmpty() == true) {
                    stateRelay.accept(STATE_LOADING)
                } else {
                    stateRelay.accept(STATE_UPDATING)
                }
            }
            .subscribe({
                Timber.i("Loaded from API")
                if (it.isEmpty()) {
                    stateRelay.accept(STATE_NO_DATA)
                } else {
                    stateRelay.accept(STATE_DATA)
                }
            }, {
                Timber.i("Request not succeed")
                if (dataRelay.value?.isEmpty() == true) {
                    //если нет данных или они пусты, то показываем ошибку на весь экран
                    stateRelay.accept(STATE_NO_CONNECTION)
                } else {
                    //не исползуем state, потому что мы не собираемся отображать snackbar каждый раз при подписке.
                    //Если получится показать, показываем
                    getView()?.showSnackbarError(R.string.error_no_connection)
                    stateRelay.accept(STATE_DATA)
                }
            }).addTo(dataCompositeDisposable)
    }

    //TODO move to view

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