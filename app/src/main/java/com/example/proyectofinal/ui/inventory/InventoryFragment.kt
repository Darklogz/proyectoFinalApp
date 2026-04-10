package com.example.proyectofinal.ui.inventory

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectofinal.R
import com.example.proyectofinal.databinding.FragmentInventoryBinding
import com.example.proyectofinal.models.Product
import com.google.firebase.firestore.FirebaseFirestore

class InventoryFragment : Fragment() {

    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: InventoryAdapter
    private var allProducts = listOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()

        setupRecyclerView()
        fetchProducts()
        setupSearch()
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = InventoryAdapter(emptyList()) { product ->
            // Handle product click (e.g., edit)
        }
        binding.rvProducts.layoutManager = LinearLayoutManager(context)
        binding.rvProducts.adapter = adapter
    }

    private fun fetchProducts() {
        db.collection("products").addSnapshotListener { value, error ->
            if (error != null) return@addSnapshotListener
            
            allProducts = value?.toObjects(Product::class.java) ?: emptyList()
            adapter.updateList(allProducts)
            updateSummary()
        }
    }

    private fun updateSummary() {
        val totalValue = allProducts.sumOf { it.price * it.stock }
        binding.tvInventorySummary.text = "${allProducts.size} productos | $${String.format("%.1fk", totalValue / 1000)} valor"
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filter(query: String) {
        val filteredList = allProducts.filter { 
            it.name.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true)
        }
        adapter.updateList(filteredList)
    }

    private fun setupListeners() {
        binding.btnMenu.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnAddProduct.setOnClickListener {
            findNavController().navigate(R.id.action_inventoryFragment_to_addProductFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}