package com.knightua.notepadapp.ui.fragments.main

import android.annotation.SuppressLint
import android.content.IntentFilter
import com.knightua.basemodule.abstracts.presenter.BasePresenter
import com.knightua.notepadapp.R
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

    fun initState() {
        mCurrentState = if (isFirstStart()) {
            UnloadedDataMainState(view!!)
        } else {
            NormalMainState(view!!)
        }
    }

    private fun isFirstStart(): Boolean {
        return true
    }

    private fun initNetworkReceiver() {
        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        mNetworkReceiver = NetworkReceiver()
        view?.context!!.registerReceiver(mNetworkReceiver, intentFilter)
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
            mCurrentState.hideError()
            loadData()
        } else {
            mCurrentState.hideProgress()
            mCurrentState.showError(R.string.error_no_connection)
        }
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
        mCurrentState.showError("Success")
    }

    private fun handleError(throwable: Throwable) {
        Timber.e(throwable)
        mCurrentState.showError(R.string.error_no_data)
    }
}