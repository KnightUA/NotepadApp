package com.knightua.notepadapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.knightua.notepadapp.databinding.ItemNoteBinding
import com.knightua.notepadapp.room.entity.Note
import java.lang.ref.WeakReference
import java.util.*

class NoteRvAdapter(notesList: List<Note>) : RecyclerView.Adapter<NoteRvAdapter.NoteViewHolder>() {

    private var mNoteList: List<Note> = notesList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflater = WeakReference(LayoutInflater.from(parent.context)).get()
        val binding = ItemNoteBinding.inflate(inflater!!, parent, false)

        return NoteViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return mNoteList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.setNote(mNoteList[position])
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var mBinding: ItemNoteBinding = DataBindingUtil.bind(itemView)!!

        fun setNote(note: Note) {
            mBinding.textViewTitle.text = note.title
            mBinding.textViewDescription.text = note.description
            mBinding.textViewDatetime.text =
                android.text.format.DateFormat.format("dd-MM-yyyy", Date(note.dateOfCreation!!))
        }
    }
}