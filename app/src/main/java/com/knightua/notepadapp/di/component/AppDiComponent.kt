package com.knightua.notepadapp.di.component

import com.knightua.notepadapp.reposotories.NoteRepository
import com.knightua.notepadapp.room.module.RoomModule
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [
        RoomModule::class
    ]
)
@Singleton
interface AppDiComponent {
    fun getNoteRepository(): NoteRepository
}