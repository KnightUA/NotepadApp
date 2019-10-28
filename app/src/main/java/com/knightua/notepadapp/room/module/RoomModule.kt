package com.knightua.notepadapp.room.module

import androidx.room.Room
import com.knightua.notepadapp.di.application.NotepadApp
import com.knightua.notepadapp.network.WebServer
import com.knightua.notepadapp.reposotories.NoteRepository
import com.knightua.notepadapp.room.dao.NoteDao
import com.knightua.notepadapp.room.database.NotepadAppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class RoomModule {

    private var notepadAppDatabase: NotepadAppDatabase =
        Room.databaseBuilder(
            NotepadApp.appContext,
            NotepadAppDatabase::class.java,
            "notepad-db"
        ).build()

    private var webServer = WebServer()

    @Singleton
    @Provides
    fun providesRoomDatabase(): NotepadAppDatabase = notepadAppDatabase

    @Singleton
    @Provides
    fun providesNoteDao(notepadAppDatabase: NotepadAppDatabase): NoteDao =
        notepadAppDatabase.noteDao()

    @Singleton
    @Provides
    fun providesNoteRepository(): NoteRepository =
        NoteRepository(notepadAppDatabase.noteDao(), webServer)
}