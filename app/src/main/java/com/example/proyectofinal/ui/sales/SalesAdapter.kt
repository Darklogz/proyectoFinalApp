package com.example.proyectofinal.ui.sales

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.databinding.ItemSaleProductBinding
import com.example.proyectofinal.models.SaleItem

class SalesAdapter(
    private var items: List<SaleItem>
) : RecyclerView.Adapter<SalesAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemSaleProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSaleProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvSaleItemQuantity.text = "${item.quantity}x "
        holder.binding.tvSaleItemName.text = item.productName
        holder.binding.tvSaleItemUnitPrice.text = "$${item.price} "
        holder.binding.tvSaleItemTotal.text = "$${item.quantity * item.price}"
    }

    override fun getItemCount() = items.size

    fun updateList(newList: List<SaleItem>) {
        items = newList
        notifyDataSetChanged()
    }
}