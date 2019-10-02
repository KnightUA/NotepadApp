package com.knightua.notepadapp.di.component

import com.knightua.basemodule.abstracts.presenter.BaseMvpPresenter
import com.knightua.basemodule.abstracts.presenter.BasePresenter
import com.knightua.basemodule.abstracts.view.BaseView
import com.knightua.notepadapp.room.module.RoomModule
import com.knightua.notepadapp.ui.activities.main.MainActivity
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [
        RoomModule::class
    ]
)
@Singleton
interface AppDiComponent {
    fun inject(basePresenter: BasePresenter<BaseView>)
}