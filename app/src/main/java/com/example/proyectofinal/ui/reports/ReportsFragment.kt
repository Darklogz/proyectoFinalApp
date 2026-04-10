package com.example.proyectofinal.ui.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectofinal.databinding.FragmentReportsBinding
import com.example.proyectofinal.models.Sale
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.android.material.tabs.TabLayout

class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: ReportsAdapter
    private var allSales = listOf<Sale>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()

        setupRecyclerView()
        setupTabs()
        fetchSales()
        
        binding.btnMenu.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        adapter = ReportsAdapter(emptyList())
        binding.rvReports.layoutManager = LinearLayoutManager(context)
        binding.rvReports.adapter = adapter
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                filterSales(tab?.position ?: 0)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun fetchSales() {
        db.collection("sales")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener
                allSales = value?.toObjects(Sale::class.java) ?: emptyList()
                filterSales(binding.tabLayout.selectedTabPosition)
            }
    }

    private fun filterSales(position: Int) {
        // Simple filtering logic for demo purposes
        // In a real app, you'd filter by date ranges
        val filtered = when (position) {
            0 -> allSales // Diario (showing all for now)
            1 -> allSales // Semanal
            2 -> allSales // Mensual
            else -> allSales
        }
        adapter.updateList(filtered)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}