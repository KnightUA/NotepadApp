package com.knightua.notepadapp.ui.fragments.note

import com.knightua.basemodule.abstracts.presenter.BasePresenter
import com.knightua.notepadapp.di.application.NotepadApp
import com.knightua.notepadapp.room.entity.Note

class NoteFragmentPresenter : BasePresenter<NoteFragmentView>() {

    private lateinit var mNote: Note

    fun saveNote() {

        if(isNoteChanged()) {
            mNote.title = getView()?.getTitle()
            mNote.description = getView()?.getDescription()
            mNote.dateOfCreation = System.currentTimeMillis()
            NotepadApp.injector.getNoteRepository().updateInDatabase(mNote)
        }
    }

    private fun isNoteChanged() : Boolean {
        val newTitle = getView()?.getTitle()
        val newDescription = getView()?.getDescription()

        return !mNote.title?.equals(newTitle)!! || !mNote.description.equals(newDescription)!!
    }

    fun showNote(note: Note) {
        mNote = note
        getView()?.showNote(note)
    }
}