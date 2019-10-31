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
import kotlin.collections.ArrayList


class NoteRvAdapter() :
    RecyclerView.Adapter<NoteRvAdapter.NoteViewHolder>() {

    private var mNoteList: ArrayList<Note> = ArrayList()
    private var mOnItemClickListener: OnItemClickListener? = null
    private var mRecentlyDeletedItems: ArrayList<Pair<Int, Note>> = ArrayList()
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

    fun update(note: Note) {
        for (i in mNoteList.indices) {
            if (mNoteList.get(i).uuid.equals(note.uuid)) {
                mNoteList[i] = note
                notifyItemChanged(i)
            }
        }
    }

    fun updateAll(notes: List<Note>) {
        for (note in notes) {
            update(note)
        }
    }

    fun addToUndo(position: Int) {
        mRecentlyDeletedItems.add(Pair(position, mNoteList.get(position)))
        deleteAt(position)
    }

    fun clearUndo() {
        mRecentlyDeletedItems.clear()
    }

    fun getRecentlyDeletedItems(): ArrayList<Pair<Int, Note>> {
        return mRecentlyDeletedItems
    }

    fun deleteAt(position: Int) {
        mNoteList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun delete(note: Note) {
        for (i in mNoteList.indices)
            if (mNoteList[i] == note) {
                deleteAt(i)
                break
            }

    }

    fun deleteAll(notes: List<Note>) {
        for (note in notes) {
            delete(note)
        }
    }

    fun add(note: Note) {
        mNoteList.add(note)
        notifyItemInserted(mNoteList.size - 1)
    }

    fun addAll(notes: List<Note>) {
        mNoteList.addAll(notes)
        notifyDataSetChanged()
    }

    fun clearAndAddAll(notes: List<Note>) {
        mNoteList.clear()
        mNoteList.addAll(notes)
        notifyDataSetChanged()
    }

    fun clearAll() {
        mNoteList.clear()
        notifyDataSetChanged()
    }

    fun undoDelete() {
        for (recentlyDeletedItem in mRecentlyDeletedItems) {
            mNoteList.add(recentlyDeletedItem.first, recentlyDeletedItem.second)
            notifyItemInserted(recentlyDeletedItem.first)
        }
        clearUndo()
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var mBinding: ItemNoteBinding = DataBindingUtil.bind(itemView)!!

        fun setNote(note: Note) {
            mBinding.textViewTitle.text = note.title
            mBinding.textViewDescription.text = note.description
            setDateOrTime(note.dateOfCreation)
        }

        fun bind(note: Note, onItemClickListener: OnItemClickListener) {
            itemView.setOnClickListener {
                onItemClickListener.onItemClick(note)
            }
        }

        private fun setDateOrTime(timeInMillis: Long?) {
            timeInMillis?.let {
                if (isCurrentDay(timeInMillis)) {
                    mBinding.textViewDatetime.text =
                        android.text.format.DateFormat.format("hh:mm", timeInMillis)
                } else {
                    mBinding.textViewDatetime.text =
                        android.text.format.DateFormat.format("dd-MM-yyyy", timeInMillis)
                }
            }
        }

        private fun isCurrentDay(timeInMillis: Long): Boolean {
            val currentCalendar = Calendar.getInstance()
            val compareCalendar = Calendar.getInstance()
            compareCalendar.timeInMillis = timeInMillis

            val currentYear = currentCalendar.get(Calendar.YEAR)
            val compareYear = compareCalendar.get(Calendar.YEAR)

            val currentDay = currentCalendar.get(Calendar.DAY_OF_YEAR)
            val compareDay = compareCalendar.get(Calendar.DAY_OF_YEAR)

            return (currentYear == compareYear && currentDay == compareDay)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: Note)
    }
}