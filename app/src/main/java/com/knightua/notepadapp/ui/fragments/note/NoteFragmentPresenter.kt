package com.knightua.notepadapp.ui.fragments.note

import com.knightua.basemodule.abstracts.presenter.BasePresenter
import com.knightua.notepadapp.di.application.NotepadApp
import com.knightua.notepadapp.room.entity.Note

class NoteFragmentPresenter : BasePresenter<NoteFragmentView>() {

    private lateinit var mNote: Note

    fun saveNote() {
        mNote.title = getView()?.getTitle()
        mNote.description = getView()?.getDescription()
        mNote.dateOfCreation = System.currentTimeMillis()
        NotepadApp.injector.getNoteRepository().updateInDatabase(mNote)
    }

    fun showNote(note: Note) {
        mNote = note
        getView()?.showNote(note)
    }
}