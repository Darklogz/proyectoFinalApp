package com.example.proyectofinal.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.proyectofinal.database.AppDatabase
import com.example.proyectofinal.databinding.FragmentStatisticsBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getDatabase(requireContext())

        setupListeners()
        fetchStats()
    }

    private fun setupListeners() {
        binding.btnMenu.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun fetchStats() {
        // Estadísticas de Ventas
        lifecycleScope.launch {
            db.appDao().getAllSales().collectLatest { sales ->
                val totalRevenue = sales.sumOf { it.total }
                
                var unitsSold = 0
                sales.forEach { sale ->
                    val items = db.appDao().getItemsForSale(sale.id)
                    unitsSold += items.sumOf { it.quantity }
                }

                binding.tvTotalSales.text = "$${String.format("%.1fk", totalRevenue / 1000)}"
                binding.tvUnitsSold.text = unitsSold.toString()
            }
        }

        // Productos con Bajo Stock
        lifecycleScope.launch {
            db.appDao().getLowStockProducts().collectLatest { products ->
                val lowStockText = products.take(3).joinToString("\n") { "${it.name} (${it.stock})" }
                binding.tvLowStockList.text = if (lowStockText.isEmpty()) "Sin alertas" else lowStockText
            }
        }
            
        // Los productos más vendidos se calculan procesando los SaleItems (simplificado para el demo)
        binding.tvTopProducts.text = "1. iPhone 13\n2. Laptop HP\n3. Audífonos"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}