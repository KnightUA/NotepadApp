package com.knightua.notepadapp.mvp.presenters

import com.knightua.basemodule.abstracts.presenter.BasePresenter
import com.knightua.notepadapp.mvp.contracts.MainActivityContract

class MainActivityPresenter : BasePresenter<MainActivityContract.View>(),
    MainActivityContract.Presenter {
}