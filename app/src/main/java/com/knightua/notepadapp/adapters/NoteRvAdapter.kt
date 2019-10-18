package com.knightua.notepadapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.knightua.notepadapp.R
import com.knightua.notepadapp.databinding.ItemNoteBinding
import com.knightua.notepadapp.room.entity.Note
import java.lang.ref.WeakReference
import java.util.*


class NoteRvAdapter() :
    RecyclerView.Adapter<NoteRvAdapter.NoteViewHolder>() {

    private var mNoteList: ArrayList<Note> = ArrayList()
    private var mOnItemClickListener: OnItemClickListener? = null
    private var mRecentlyDeletedItem: Note? = null
    private var mRecentlyDeletedItemPosition: Int? = null
    private var mView: View? = null

    constructor(onItemClickListener: OnItemClickListener) : this() {
        mOnItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflater = WeakReference(LayoutInflater.from(parent.context)).get()
        val binding = ItemNoteBinding.inflate(inflater!!, parent, false)

        mView = binding.root

        return NoteViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return mNoteList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.setNote(note = mNoteList[position])
        mOnItemClickListener?.let {
            holder.bind(mNoteList[position], it)
        }
    }

    fun getItemAt(position: Int): Note {
        return mNoteList.get(position)
    }

    fun deleteAt(position: Int) {
        mRecentlyDeletedItem = mNoteList.get(position)
        mRecentlyDeletedItemPosition = position
        mNoteList.removeAt(position)
        notifyItemRemoved(position)
        showUndoSnackbar()
    }

    fun add(note: Note) {
        mNoteList.add(note)
        notifyItemInserted(mNoteList.size - 1)
    }

    fun addAll(notes: List<Note>) {
        mNoteList.addAll(notes)
        notifyDataSetChanged()
    }

    private fun showUndoSnackbar() {
        val snackbar = Snackbar.make(
            mView!!
            , R.string.snack_bar_text,
            Snackbar.LENGTH_LONG
        )
        snackbar.setAction(R.string.snack_bar_undo, { v -> undoDelete() })
        snackbar.show()
    }

    private fun undoDelete() {
        mNoteList.add(
            mRecentlyDeletedItemPosition!!,
            mRecentlyDeletedItem!!
        )
        notifyItemInserted(mRecentlyDeletedItemPosition!!)
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var mBinding: ItemNoteBinding = DataBindingUtil.bind(itemView)!!

        fun setNote(note: Note) {
            mBinding.textViewTitle.text = note.title
            mBinding.textViewDescription.text = note.description
            mBinding.textViewDatetime.text =
                android.text.format.DateFormat.format("dd-MM-yyyy", Date(note.dateOfCreation!!))
        }

        fun bind(note: Note, onItemClickListener: OnItemClickListener) {
            itemView.setOnClickListener {
                onItemClickListener.onItemClick(note)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: Note)
    }
}