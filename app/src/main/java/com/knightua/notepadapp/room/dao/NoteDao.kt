package com.knightua.notepadapp.room.dao

import androidx.room.*
import com.knightua.notepadapp.room.entity.Note
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface NoteDao {

    @Query("SELECT * FROM note")
    fun getAll(): Flowable<List<Note>>

    @Query("SELECT * FROM note WHERE uuid = :uuid")
    fun getById(uuid: String): Single<Note>

    @Query("SELECT COUNT(uuid) FROM note")
    fun getCount(): Single<Int>

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

    @Query("DELETE FROM note WHERE uuid = :uuid")
    fun deleteById(uuid: String)

    @Query("DELETE FROM note")
    fun clear()
}