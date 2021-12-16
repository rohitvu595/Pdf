package com.pdf.modules.pdf

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pdf.databinding.ItemPdfBinding
import com.pdf.modules.pdf.model.Item


class PdfAdapter internal constructor(
    var context: Context,
    var itemlist: MutableList<Item>
) : RecyclerView.Adapter<PdfAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        var binding = ItemPdfBinding.inflate(layoutInflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount() = itemlist.size

    class MyViewHolder(itemOwnedBinding: ItemPdfBinding) :
        RecyclerView.ViewHolder(itemOwnedBinding.root) {
        var binding: ItemPdfBinding

        init {
            this.binding = itemOwnedBinding
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.item = itemlist.get(position)

        holder.binding.root.setOnClickListener {
//            adapterItemClick.onItemClick(position, itemlist.get(position).Id, "false")
        }
    }

}
