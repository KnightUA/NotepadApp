package com.knightua.notepadapp.ui.fragments.note

import com.knightua.basemodule.abstracts.view.BaseView
import com.knightua.notepadapp.room.entity.Note

interface NoteFragmentView : BaseView {
    fun showNote(note: Note)
    fun showSaved()
    fun getTitle(): String?
    fun getDescription(): String?
}