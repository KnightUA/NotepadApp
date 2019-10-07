package com.knightua.notepadapp.network

import com.knightua.notepadapp.room.entity.Note
import io.reactivex.Observable
import java.util.*

class WebServer {

    fun getAllNotes(): Observable<List<Note>> {
        return Observable.create { emitter ->
            val notes = getRandomDataSituation()

            //Simulate getting data
            Thread.sleep(5000L)

            if (notes.isEmpty())
                emitter.onError(Throwable("Data didn't receive from Api..."))
            else
                emitter.onNext(notes)

            emitter.onComplete()
        }
    }

    private fun getDefaultNotes(): List<Note> {
        val notes: ArrayList<Note> = arrayListOf()

        notes.add(Note(1, "John", "My name is John", 10))
        notes.add(Note(2, "Martin", "My name is Martin", 100))
        notes.add(
            Note(
                3,
                "Thor",
                "My name is Thor. My name is Thor. My name is Thor. My name is Thor. My name is Thor",
                1000
            )
        )

        return notes.toList()
    }

    private fun getRandomDataSituation(): List<Note> {
        val random = Random()

        if (isEmptyListSituation(random.nextInt(100)))
            return emptyList()

        return getDefaultNotes()
    }

    private fun isEmptyListSituation(value: Int): Boolean {
        return value > 95
    }
}