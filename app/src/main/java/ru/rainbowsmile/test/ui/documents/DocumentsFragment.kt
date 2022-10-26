package ru.rainbowsmile.test.ui.documents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.rainbowsmile.test.R
import ru.rainbowsmile.test.databinding.FragmentDocumentsBinding
import ru.rainbowsmile.test.model.ScreenState

@AndroidEntryPoint
class DocumentsFragment : Fragment(), DocumentsAdapter.DocumentListener {

    private var _binding: FragmentDocumentsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DocumentsViewModel by viewModels()
    private val adapter by lazy { DocumentsAdapter(this) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDocumentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.documentsList.adapter = adapter

        viewModel.screenState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ScreenState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.documentsList.visibility = View.GONE
                }
                is ScreenState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.documentsList.visibility = View.VISIBLE
                }
                is ScreenState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        context,
                        "Exception: ${state.exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        viewModel.documentItems.observe(viewLifecycleOwner) { documents ->
            adapter.submitList(documents)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        binding.copyAmount.text =
            getString(R.string.copies_amount_text_template, viewModel.copiesAmount)
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.sortDocuments()
        super.onSaveInstanceState(outState)
    }

    override fun duplicateTop(position: Int) {
        viewModel.duplicatedDocumentAsFirst(position)
        binding.copyAmount.text =
            getString(R.string.copies_amount_text_template, viewModel.copiesAmount)
        adapter.submitList(viewModel.documentItems.value!!) {
            binding.documentsList.smoothScrollToPosition(0)
        }
    }

    override fun duplicateBottom(position: Int) {
        viewModel.duplicateDocumentAsLast(position)
        binding.copyAmount.text =
            getString(R.string.copies_amount_text_template, viewModel.copiesAmount)
        binding.documentsList.smoothScrollToPosition(adapter.itemCount)
    }
}
