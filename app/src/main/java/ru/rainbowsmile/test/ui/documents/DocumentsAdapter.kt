package ru.rainbowsmile.test.ui.documents

import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.rainbowsmile.test.R
import ru.rainbowsmile.test.databinding.DocumentItemBinding
import ru.rainbowsmile.test.model.Document
import ru.rainbowsmile.test.utils.Constants

class DocumentsAdapter(
    private val listener: DocumentListener
) : RecyclerView.Adapter<DocumentsAdapter.DocumentViewHolder>() {

    private val mDiffer = AsyncListDiffer(this, DocumentItemDiffCallback())

    class DocumentViewHolder(
        private val binding: DocumentItemBinding,
        private val listener: DocumentListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(document: Document) {
            binding.apply {
                title.text = Constants.RECORD_TEXT_TEMPLATE.format(document.idRecord)
                routeNumber.text = Constants.ROUTE_TEXT_TEMPLATE.format(document.numRoute)
                orderNumber.text = Constants.ORDER_TEXT_TEMPLATE.format(document.numOrder)
                options.setOnClickListener {
                    val popup = PopupMenu(itemView.context, it)
                    val inflater: MenuInflater = popup.menuInflater
                    inflater.inflate(R.menu.documents_options, popup.menu)
                    popup.show()

                    popup.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.duplicate_top -> listener.duplicateTop(adapterPosition)
                            R.id.duplicate_bottom -> listener.duplicateBottom(adapterPosition)
                            else -> {}
                        }
                        true
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val binding =
            DocumentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DocumentViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        holder.bind(mDiffer.currentList[position])
    }

    override fun getItemCount(): Int = mDiffer.currentList.size

    fun submitList(currencies: List<Document>) {
        mDiffer.submitList(currencies)
    }

    fun submitList(currencies: List<Document>, callback: () -> Unit) {
        mDiffer.submitList(currencies) {
            callback()
        }
    }

    class DocumentItemDiffCallback :
        DiffUtil.ItemCallback<Document>() {

        override fun areItemsTheSame(oldItem: Document, newItem: Document): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Document, newItem: Document): Boolean {
            return oldItem == newItem
        }
    }

    interface DocumentListener {
        fun duplicateTop(position: Int)
        fun duplicateBottom(position: Int)
    }
}
