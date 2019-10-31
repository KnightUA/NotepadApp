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

        notes.add(Note(UUID.fromString("331aecc0-faf1-11e9-8f0b-362b9e155667").toString(),"Stanislav", "My name is Stanislav", 876690000000))
        notes.add(Note(UUID.fromString("331aef7c-faf1-11e9-8f0b-362b9e155667").toString(),"Martin", "My name is Martin", 1356040800000))
        notes.add(Note(UUID.fromString("331af0d0-faf1-11e9-8f0b-362b9e155667").toString(),"Thor", "My name is Thor", 1302987600000))

        val currentTime = System.currentTimeMillis().toInt()

        notes.add(Note(UUID.randomUUID().toString(), "Random New Note", "This is random note", Random().nextInt(currentTime).toLong()))

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