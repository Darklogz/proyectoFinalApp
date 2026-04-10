package com.example.proyectofinal.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.proyectofinal.R
import com.example.proyectofinal.databinding.FragmentMainMenuBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainMenuFragment : Fragment() {

    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

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
        db = FirebaseFirestore.getInstance()

        setupSummary()
        setupButtons()
    }

    private fun setupSummary() {
        // Fetch real data from Firestore
        db.collection("products").addSnapshotListener { value, _ ->
            val count = value?.size() ?: 0
            binding.cardTotalProducts.tvValue.text = count.toString()
            binding.cardTotalProducts.tvLabel.text = "Productos"
            
            val lowStockCount = value?.documents?.count { 
                val stock = it.getLong("stock") ?: 0
                val minStock = it.getLong("minStock") ?: 0
                stock <= minStock
            } ?: 0
            binding.cardLowStock.tvValue.text = lowStockCount.toString()
            binding.cardLowStock.tvLabel.text = "Stock bajo"
            binding.cardLowStock.tvValue.setTextColor(resources.getColor(R.color.danger_red, null))
        }

        db.collection("sales").addSnapshotListener { value, _ ->
            var totalToday = 0.0
            value?.forEach { 
                totalToday += it.getDouble("total") ?: 0.0
            }
            binding.cardTodaySales.tvValue.text = "$${String.format("%.1fk", totalToday / 1000)}"
            binding.cardTodaySales.tvLabel.text = "Ventas hoy"
        }
    }

    private fun setupButtons() {
        binding.btnInventory.root.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_inventoryFragment)
        }
        binding.btnSales.root.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_salesFragment)
        }
        binding.btnEntries.root.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_addProductFragment)
        }
        binding.btnReports.root.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_reportsFragment)
        }
        binding.btnStats.root.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_statisticsFragment)
        }
        binding.btnConfig.root.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_usersFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}