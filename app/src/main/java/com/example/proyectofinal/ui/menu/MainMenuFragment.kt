package com.example.proyectofinal.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectofinal.R
import com.example.proyectofinal.database.AppDatabase
import com.example.proyectofinal.databinding.FragmentMainMenuBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainMenuFragment : Fragment() {

    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: AppDatabase
    private lateinit var lowStockAdapter: LowStockAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = AppDatabase.getDatabase(requireContext())

        setupRecyclerView()
        setupSummary()
        setupButtons()
    }

    private fun setupRecyclerView() {
        lowStockAdapter = LowStockAdapter(emptyList())
        binding.rvLowStock.layoutManager = LinearLayoutManager(context)
        binding.rvLowStock.adapter = lowStockAdapter
    }

    private fun setupSummary() {
        lifecycleScope.launch {
            db.appDao().getAllProducts().collectLatest { products ->
                binding.cardTotalProducts.tvValue.text = products.size.toString()
                binding.cardTotalProducts.tvLabel.text = "Productos"
                binding.cardTotalProducts.ivIcon.setImageResource(R.drawable.ic_inventory)
                
                val lowStockProducts = products.filter { it.stock <= it.minStock }
                binding.cardLowStock.tvValue.text = lowStockProducts.size.toString()
                binding.cardLowStock.tvLabel.text = "Stock bajo"
                binding.cardLowStock.ivIcon.setImageResource(R.drawable.ic_warning)
                binding.cardLowStock.ivIcon.setColorFilter(resources.getColor(R.color.warning_orange, null))
                binding.cardLowStock.tvValue.setTextColor(resources.getColor(R.color.danger_red, null))
                
                lowStockAdapter.updateList(lowStockProducts.take(5))
            }
        }

        lifecycleScope.launch {
            db.appDao().getAllSales().collectLatest { sales ->
                val totalToday = sales.filter { 
                    val diff = System.currentTimeMillis() - it.timestamp
                    diff < 24 * 60 * 60 * 1000 
                }.sumOf { it.total }
                
                binding.cardTodaySales.tvValue.text = "$${String.format("%.1fk", totalToday / 1000)}"
                binding.cardTodaySales.tvLabel.text = "Ventas hoy"
                binding.cardTodaySales.ivIcon.setImageResource(R.drawable.ic_sales)
                binding.cardTodaySales.ivIcon.setColorFilter(resources.getColor(R.color.success_green, null))
            }
        }
    }

    private fun setupButtons() {
        // Inventario
        binding.btnNavInventory.tvLabel.text = "Inventario"
        binding.btnNavInventory.ivIcon.setImageResource(R.drawable.ic_inventory)
        binding.btnNavInventory.root.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_inventoryFragment)
        }

        // Ventas
        binding.btnNavSales.tvLabel.text = "Ventas"
        binding.btnNavSales.ivIcon.setImageResource(R.drawable.ic_sales)
        binding.btnNavSales.ivIcon.setColorFilter(resources.getColor(R.color.success_green, null))
        binding.btnNavSales.root.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_salesFragment)
        }

        // Entradas (Agregar Producto)
        binding.btnNavEntries.tvLabel.text = "Entradas"
        binding.btnNavEntries.ivIcon.setImageResource(R.drawable.ic_add)
        binding.btnNavEntries.ivIcon.setColorFilter(resources.getColor(R.color.warning_orange, null))
        binding.btnNavEntries.root.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_addProductFragment)
        }

        // Reportes
        binding.btnNavReports.tvLabel.text = "Reportes"
        binding.btnNavReports.ivIcon.setImageResource(R.drawable.ic_reports)
        binding.btnNavReports.root.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_reportsFragment)
        }

        // Estadísticas
        binding.btnNavStats.tvLabel.text = "Estadísticas"
        binding.btnNavStats.ivIcon.setImageResource(R.drawable.ic_reports)
        binding.btnNavStats.ivIcon.setColorFilter(resources.getColor(R.color.success_green, null))
        binding.btnNavStats.root.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_statisticsFragment)
        }

        // Configuración (Usuarios)
        binding.btnNavConfig.tvLabel.text = "Configuración"
        binding.btnNavConfig.ivIcon.setImageResource(R.drawable.ic_person)
        binding.btnNavConfig.root.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_usersFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}