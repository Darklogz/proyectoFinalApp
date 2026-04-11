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
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var db: AppDatabase
    private lateinit var lowStockAdapter: LowStockAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        return binding!!.root
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
        binding?.rvLowStock?.layoutManager = LinearLayoutManager(context)
        binding?.rvLowStock?.adapter = lowStockAdapter
    }

    private fun setupSummary() {
        viewLifecycleOwner.lifecycleScope.launch {
            db.appDao().getAllProducts().collectLatest { products ->
                binding?.let { b ->
                    b.cardTotalProducts.tvValue.text = products.size.toString()
                    b.cardTotalProducts.tvLabel.text = "Productos"
                    b.cardTotalProducts.ivIcon.setImageResource(R.drawable.ic_inventory)
                    
                    val criticalStockProducts = products.filter { 
                        it.stock < (it.minStock * 0.4)
                    }
                    
                    b.cardLowStock.tvValue.text = criticalStockProducts.size.toString()
                    b.cardLowStock.tvLabel.text = "Stock Crítico"
                    b.cardLowStock.ivIcon.setImageResource(R.drawable.ic_warning)
                    b.cardLowStock.ivIcon.setColorFilter(resources.getColor(R.color.danger_red, null))
                    b.cardLowStock.tvValue.setTextColor(resources.getColor(R.color.danger_red, null))
                    
                    lowStockAdapter.updateList(criticalStockProducts)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            db.appDao().getAllSales().collectLatest { sales ->
                binding?.let { b ->
                    val totalToday = sales.filter { 
                        val diff = System.currentTimeMillis() - it.timestamp
                        diff < 24 * 60 * 60 * 1000 
                    }.sumOf { it.total }
                    
                    b.cardTodaySales.tvValue.text = "$${String.format("%.1fk", totalToday / 1000)}"
                    b.cardTodaySales.tvLabel.text = "Ventas hoy"
                    b.cardTodaySales.ivIcon.setImageResource(R.drawable.ic_sales)
                    b.cardTodaySales.ivIcon.setColorFilter(resources.getColor(R.color.success_green, null))
                }
            }
        }
    }

    private fun setupButtons() {
        binding?.let { b ->
            b.btnNavInventory.tvLabel.text = "Inventario"
            b.btnNavInventory.ivIcon.setImageResource(R.drawable.ic_inventory)
            b.btnNavInventory.root.setOnClickListener {
                findNavController().navigate(R.id.action_mainMenuFragment_to_inventoryFragment)
            }

            b.btnNavSales.tvLabel.text = "Ventas"
            b.btnNavSales.ivIcon.setImageResource(R.drawable.ic_sales)
            b.btnNavSales.ivIcon.setColorFilter(resources.getColor(R.color.success_green, null))
            b.btnNavSales.root.setOnClickListener {
                findNavController().navigate(R.id.action_mainMenuFragment_to_salesFragment)
            }

            b.btnNavEntries.tvLabel.text = "Entradas"
            b.btnNavEntries.ivIcon.setImageResource(R.drawable.ic_add)
            b.btnNavEntries.ivIcon.setColorFilter(resources.getColor(R.color.warning_orange, null))
            b.btnNavEntries.root.setOnClickListener {
                findNavController().navigate(R.id.action_mainMenuFragment_to_addProductFragment)
            }

            b.btnNavReports.tvLabel.text = "Reportes"
            b.btnNavReports.ivIcon.setImageResource(R.drawable.ic_reports)
            b.btnNavReports.root.setOnClickListener {
                findNavController().navigate(R.id.action_mainMenuFragment_to_reportsFragment)
            }

            b.btnNavStats.tvLabel.text = "Estadísticas"
            b.btnNavStats.ivIcon.setImageResource(R.drawable.ic_reports) 
            b.btnNavStats.ivIcon.setColorFilter(resources.getColor(R.color.success_green, null))
            b.btnNavStats.root.setOnClickListener {
                findNavController().navigate(R.id.action_mainMenuFragment_to_statisticsFragment)
            }

            b.btnNavConfig.tvLabel.text = "Usuarios"
            b.btnNavConfig.ivIcon.setImageResource(R.drawable.ic_person)
            b.btnNavConfig.root.setOnClickListener {
                findNavController().navigate(R.id.action_mainMenuFragment_to_usersFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}