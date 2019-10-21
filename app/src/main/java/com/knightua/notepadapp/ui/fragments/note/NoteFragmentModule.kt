package com.knightua.notepadapp.ui.fragments.note

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NoteFragmentModule {

    @Singleton
    @Provides
    fun providesNoteFragmentPresenter(): NoteFragmentPresenter =
        NoteFragmentPresenter()
}