package com.knightua.notepadapp.ui.fragments.note

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NoteFragmentModule::class
    ]
)

interface NoteFragmentComponent {

    fun inject(noteFragment: NoteFragment)
}