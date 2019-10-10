package com.knightua.notepadapp.ui.fragments.main

import SwipeToDeleteCallback
import android.annotation.SuppressLint
import android.content.IntentFilter
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.knightua.basemodule.abstracts.presenter.BasePresenter
import com.knightua.notepadapp.R
import com.knightua.notepadapp.adapters.NoteRvAdapter
import com.knightua.notepadapp.di.application.NotepadApp
import com.knightua.notepadapp.receivers.NetworkReceiver
import com.knightua.notepadapp.room.entity.Note
import com.knightua.notepadapp.states.main.BaseMainState
import com.knightua.notepadapp.states.main.NormalMainState
import com.knightua.notepadapp.states.main.UnloadedDataMainState
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber

class MainFragmentPresenter : BasePresenter<MainFragment>(),
    NetworkReceiver.NetworkReceiverListener {

    private lateinit var mNetworkReceiver: NetworkReceiver
    private lateinit var mCurrentState: BaseMainState
    private val onItemNoteClickListener: NoteRvAdapter.OnItemClickListener =
        object : NoteRvAdapter.OnItemClickListener {
            override fun onItemClick(item: Note) {
                Toast.makeText(
                    view?.context,
                    String.format("Clicked on %s", item.title),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    @SuppressLint("CheckResult")
    fun initState() {
        NotepadApp.injector.getNoteRepository().getAllFromDatabase()
            .subscribe {
                if (it.isNotEmpty()) {
                    mCurrentState = NormalMainState(view!!)
                    initAdapter(it)
                } else {
                    //TODO NOT Working at first time
                    mCurrentState = UnloadedDataMainState(view!!)
                }
                registerReceivers()
            }
    }

    private fun initNetworkReceiver() {
        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        mNetworkReceiver = NetworkReceiver()
        view?.context!!.registerReceiver(mNetworkReceiver, intentFilter)
        NetworkReceiver.networkReceiverListener = this
    }

    fun registerReceivers() {
        Timber.i("registerReceivers")
        initNetworkReceiver()
    }

    fun unregisterReceivers() {
        view?.context?.unregisterReceiver(mNetworkReceiver)
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {

        if (isConnected) {
            mCurrentState.hideError()
            loadData()
        } else {
            mCurrentState.hideProgress()
            mCurrentState.showError(R.string.error_no_connection)
        }
    }

    fun addDefaultNote() {
        val defaultNote = Note("Title", "Description", System.currentTimeMillis())
        NotepadApp.injector.getNoteRepository()
            .insertInDatabase(defaultNote)
        (view?.mBinding?.recyclerViewNotes?.adapter as NoteRvAdapter).add(defaultNote)
    }

    @SuppressLint("CheckResult")
    fun loadData() {
        NotepadApp.injector.getNoteRepository().getAllFromApi()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnTerminate {
                mCurrentState.hideProgress()
            }
            .doOnSubscribe {
                mCurrentState.showProgress()
            }
            .subscribe(::handleData, ::handleError)
    }

    private fun handleData(notes: List<Note>) {
        if (mCurrentState is UnloadedDataMainState)
            initAdapter(notes)
        else
            (view?.mBinding?.recyclerViewNotes?.adapter as NoteRvAdapter).addAll(notes)
        view?.mBinding?.recyclerViewNotes?.adapter?.notifyDataSetChanged()
    }

    private fun handleError(throwable: Throwable) {
        Timber.e(throwable)
        mCurrentState.showError(R.string.error_no_data)
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