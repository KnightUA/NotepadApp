package com.knightua.notepadapp.room.dao

import androidx.room.*
import com.knightua.notepadapp.room.entity.Note

@Dao
interface NoteDao {

    @Query("SELECT * FROM note")
    fun getAll(): List<Note>

    @Query("SELECT * FROM note WHERE id = :id")
    fun getById(id: Long): Note

    @Insert
    fun insertAll(vararg notes: Note)

    @Insert
    fun insert(note: Note)

    @Update
    fun update(note: Note)

    @Delete
    fun deleteAll(vararg notes: Note)

    @Delete
    fun delete(note: Note)
}