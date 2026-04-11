package com.example.proyectofinal.ui.inventory

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectofinal.R
import com.example.proyectofinal.database.AppDatabase
import com.example.proyectofinal.databinding.FragmentInventoryBinding
import com.example.proyectofinal.models.Product
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class InventoryFragment : Fragment() {

    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase
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
        db = AppDatabase.getDatabase(requireContext())

        setupRecyclerView()
        fetchProducts()
        setupSearch()
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = InventoryAdapter(
            products = emptyList(),
            onEditStockClick = { product ->
                showEditStockDialog(product)
            },
            onItemClick = { product ->
                // Handle general item click if needed
            }
        )
        binding.rvProducts.layoutManager = LinearLayoutManager(context)
        binding.rvProducts.adapter = adapter
    }

    private fun showEditStockDialog(product: Product) {
        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.hint = "Nueva cantidad"
        input.setText(product.stock.toString())

        AlertDialog.Builder(requireContext())
            .setTitle("Resurtir Producto")
            .setMessage("Ingresa la cantidad actual de ${product.name}")
            .setView(input)
            .setPositiveButton("Guardar") { _, _ ->
                val newStock = input.text.toString().toIntOrNull()
                if (newStock != null) {
                    updateProductStock(product, newStock)
                } else {
                    Toast.makeText(context, "Cantidad no válida", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun updateProductStock(product: Product, newStock: Int) {
        lifecycleScope.launch {
            db.appDao().updateProduct(product.copy(stock = newStock))
            Toast.makeText(context, "Stock actualizado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchProducts() {
        lifecycleScope.launch {
            db.appDao().getAllProducts().collectLatest { products ->
                if (_binding == null) return@collectLatest
                allProducts = products
                adapter.updateList(allProducts)
                updateSummary()
            }
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