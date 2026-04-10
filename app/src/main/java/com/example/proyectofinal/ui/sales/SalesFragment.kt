package com.example.proyectofinal.ui.sales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectofinal.databinding.FragmentSalesBinding
import com.example.proyectofinal.models.Product
import com.example.proyectofinal.models.Sale
import com.example.proyectofinal.models.SaleItem
import com.google.firebase.firestore.FirebaseFirestore

class SalesFragment : Fragment() {

    private var _binding: FragmentSalesBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: SalesAdapter
    private val saleItems = mutableListOf<SaleItem>()
    private var allProducts = listOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSalesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()

        setupRecyclerView()
        setupSearch()
        setupListeners()
        fetchProducts()
    }

    private fun setupRecyclerView() {
        adapter = SalesAdapter(saleItems)
        binding.rvSaleItems.layoutManager = LinearLayoutManager(context)
        binding.rvSaleItems.adapter = adapter
    }

    private fun fetchProducts() {
        db.collection("products").get().addOnSuccessListener { value ->
            allProducts = value.toObjects(Product::class.java)
            val productNames = allProducts.map { it.name }
            val searchAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, productNames)
            binding.etSearchProduct.setAdapter(searchAdapter)
        }
    }

    private fun setupSearch() {
        binding.etSearchProduct.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position) as String
            val product = allProducts.find { it.name == selectedName }
            product?.let { addProductToSale(it) }
            binding.etSearchProduct.setText("")
        }
    }

    private fun addProductToSale(product: Product) {
        if (product.stock <= 0) {
            Toast.makeText(context, "Producto sin stock", Toast.LENGTH_SHORT).show()
            return
        }

        val existingItem = saleItems.find { it.productId == product.id }
        if (existingItem != null) {
            val index = saleItems.indexOf(existingItem)
            saleItems[index] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            saleItems.add(SaleItem(product.id, product.name, 1, product.price))
        }
        
        adapter.updateList(saleItems)
        calculateTotals()
    }

    private fun calculateTotals() {
        val subtotal = saleItems.sumOf { it.price * it.quantity }
        val tax = subtotal * 0.10
        val total = subtotal + tax

        binding.tvSubtotal.text = "$${String.format("%.2f", subtotal)}"
        binding.tvTax.text = "$${String.format("%.2f", tax)}"
        binding.tvTotal.text = "$${String.format("%.2f", total)}"
    }

    private fun setupListeners() {
        binding.btnMenu.setOnClickListener { findNavController().navigateUp() }
        
        binding.btnFinalize.setOnClickListener {
            if (saleItems.isEmpty()) return@setOnClickListener
            finalizeSale()
        }
    }

    private fun finalizeSale() {
        val total = saleItems.sumOf { it.price * it.quantity } * 1.10
        val sale = Sale(
            items = saleItems.toList(),
            total = total,
            paymentMethod = "Efectivo" // Simplified for now
        )

        db.runTransaction { transaction ->
            // Save Sale
            val saleRef = db.collection("sales").document()
            transaction.set(saleRef, sale)

            // Update Stock
            saleItems.forEach { item ->
                val productRef = db.collection("products").document(item.productId)
                val snapshot = transaction.get(productRef)
                val currentStock = snapshot.getLong("stock") ?: 0
                transaction.update(productRef, "stock", currentStock - item.quantity)
            }
        }.addOnSuccessListener {
            Toast.makeText(context, "Venta realizada", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }.addOnFailureListener {
            Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}