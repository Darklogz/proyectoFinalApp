package com.example.proyectofinal.ui.inventory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.databinding.ItemProductBinding
import com.example.proyectofinal.models.Product

class InventoryAdapter(
    private var products: List<Product>,
    private val onEditStockClick: (Product) -> Unit,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.binding.tvProductName.text = product.name
        holder.binding.tvProductPrice.text = "$${product.price}"
        holder.binding.tvProductStock.text = product.stock.toString()

        holder.binding.btnEditStock.setOnClickListener { onEditStockClick(product) }
        holder.itemView.setOnClickListener { onItemClick(product) }
    }

    override fun getItemCount() = products.size

    fun updateList(newList: List<Product>) {
        products = newList
        notifyDataSetChanged()
    }
}