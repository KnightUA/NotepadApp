package com.knightua.notepadapp.di.component

import com.knightua.notepadapp.di.modules.MainActivityModule
import com.knightua.notepadapp.di.modules.RoomModule
import com.knightua.notepadapp.mvp.views.activity.MainActivity
import com.knightua.notepadapp.room.dao.NoteDao
import com.knightua.notepadapp.room.database.NotepadAppDatabase
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [
        MainActivityModule::class,
        RoomModule::class
    ]
)
@Singleton
interface AppDiComponent {
    fun inject(mainActivity: MainActivity)
    fun noteDao(): NoteDao
    fun notepadAppDatabase(): NotepadAppDatabase
}