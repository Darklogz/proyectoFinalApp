package com.example.proyectofinal.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.proyectofinal.databinding.FragmentStatisticsBinding
import com.example.proyectofinal.models.Product
import com.example.proyectofinal.models.Sale
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()

        setupListeners()
        fetchStats()
    }

    private fun setupListeners() {
        binding.btnMenu.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun fetchStats() {
        // Fetch Top Sales (simulated by fetching all sales and processing)
        db.collection("sales").get().addOnSuccessListener { value ->
            val sales = value.toObjects(Sale::class.java)
            val totalRevenue = sales.sumOf { it.total }
            val unitsSold = sales.sumOf { it.items.sumOf { item -> item.quantity } }

            binding.tvTotalSales.text = "$${String.format("%.1fk", totalRevenue / 1000)}"
            binding.tvUnitsSold.text = unitsSold.toString()
        }

        // Fetch Low Stock Products
        db.collection("products")
            .whereLessThanOrEqualTo("stock", 5) // Simple criteria for demo
            .limit(3)
            .get()
            .addOnSuccessListener { value ->
                val products = value.toObjects(Product::class.java)
                val lowStockText = products.joinToString("\n") { "${it.name} (${it.stock})" }
                binding.tvLowStockList.text = if (lowStockText.isEmpty()) "Sin alertas" else lowStockText
            }
            
        // Mocking top products for now as it requires complex aggregation
        binding.tvTopProducts.text = "1. iPhone 13\n2. Laptop HP\n3. Audífonos"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}