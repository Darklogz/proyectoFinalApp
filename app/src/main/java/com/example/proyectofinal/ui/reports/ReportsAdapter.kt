package com.example.proyectofinal.ui.reports

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.databinding.ItemSaleReportBinding
import com.example.proyectofinal.models.Sale
import java.text.SimpleDateFormat
import java.util.Locale

class ReportsAdapter(
    private var sales: List<Sale>
) : RecyclerView.Adapter<ReportsAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemSaleReportBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSaleReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sale = sales[position]
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        
        holder.binding.tvSaleId.text = "#${sale.id.takeLast(6).uppercase()}"
        holder.binding.tvSaleDate.text = sdf.format(sale.timestamp.toDate())
        holder.binding.tvSaleItemsSummary.text = "${sale.items.size} productos vendidos"
        holder.binding.tvSaleTotal.text = "$${String.format("%.2f", sale.total)}"
    }

    override fun getItemCount() = sales.size

    fun updateList(newList: List<Sale>) {
        sales = newList
        notifyDataSetChanged()
    }
}