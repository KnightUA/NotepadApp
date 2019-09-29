package com.knightua.notepadapp.room.dao

import androidx.room.*
import com.knightua.notepadapp.room.entity.Note
import io.reactivex.Single

@Dao
interface NoteDao {

    @Query("SELECT * FROM note")
    fun getAll(): Single<List<Note>>

    @Query("SELECT * FROM note WHERE id = :id")
    fun getById(id: Long): Single<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(notes: List<Note>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: Note)

    @Update
    fun update(note: Note)

    @Delete
    fun deleteAll(notes: List<Note>)

    @Delete
    fun delete(note: Note)

    @Query("DELETE FROM note WHERE id = :id")
    fun deleteById(id: Long)

    @Query("DELETE FROM note")
    fun clear()
}