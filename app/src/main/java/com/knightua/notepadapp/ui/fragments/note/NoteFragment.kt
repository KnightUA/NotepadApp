package com.knightua.notepadapp.ui.fragments.note

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.knightua.basemodule.abstracts.view.BaseFragment
import com.knightua.notepadapp.R
import com.knightua.notepadapp.databinding.FragmentNoteBinding
import com.knightua.notepadapp.room.entity.Note
import java.text.SimpleDateFormat
import javax.inject.Inject

class NoteFragment : BaseFragment(), NoteFragmentView, View.OnClickListener {

    @Inject
    lateinit var presenter: NoteFragmentPresenter

    private lateinit var mBinding: FragmentNoteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_note, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DaggerNoteFragmentComponent.create().inject(this)
        presenter.attach(this)

        arguments?.let {
            it.getParcelable<Note>(BUNDLE_NOTE)?.let { note ->
                presenter.showNote(note)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detach()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.note, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.menu_item_action_save -> {
                presenter.saveNote()
                findNavController().popBackStack()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onClick(p0: View?) {
    }

    override fun showNote(note: Note) {
        mBinding.editTextTitle.setText(note.title)
        mBinding.editTextDescription.setText(note.description)
        mBinding.textViewDate.setText(SimpleDateFormat("dd.MM.yyyy").format(note.dateOfCreation))
    }

    override fun getTitle(): String? {
        return mBinding.editTextTitle.text.toString()
    }

    override fun getDescription(): String? {
        return mBinding.editTextDescription.text.toString()
    }

    override fun showSaved() {
        Toast.makeText(activity, getString(R.string.text_saved), Toast.LENGTH_LONG).show()
    }

    companion object {

        const val BUNDLE_NOTE = "bundle_note"

    }
}