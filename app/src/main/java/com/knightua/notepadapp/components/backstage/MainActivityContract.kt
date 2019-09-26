package com.knightua.notepadapp.components.backstage

import com.knightua.basemodule.abstracts.presenter.BaseMvpPresenter
import com.knightua.basemodule.abstracts.view.BaseView

interface MainActivityContract {
    interface Presenter : BaseMvpPresenter<View>
    interface View : BaseView
}