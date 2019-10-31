package com.knightua.notepadapp.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.knightua.notepadapp.room.dao.NoteDao
import com.knightua.notepadapp.room.entity.Note

@Database(entities = arrayOf(Note::class), version = 2)
abstract class NotepadAppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}