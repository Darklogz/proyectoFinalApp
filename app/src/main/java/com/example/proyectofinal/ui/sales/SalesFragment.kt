package com.example.proyectofinal.ui.sales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectofinal.database.AppDatabase
import com.example.proyectofinal.databinding.FragmentSalesBinding
import com.example.proyectofinal.models.Product
import com.example.proyectofinal.models.Sale
import com.example.proyectofinal.models.SaleItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SalesFragment : Fragment() {

    private var _binding: FragmentSalesBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase
    private lateinit var adapter: SalesAdapter
    private val saleItemsList = mutableListOf<SaleItem>()
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
        db = AppDatabase.getDatabase(requireContext())

        setupRecyclerView()
        setupSearch()
        setupListeners()
        fetchProducts()
    }

    private fun setupRecyclerView() {
        adapter = SalesAdapter(saleItemsList)
        binding.rvSaleItems.layoutManager = LinearLayoutManager(context)
        binding.rvSaleItems.adapter = adapter
    }

    private fun fetchProducts() {
        lifecycleScope.launch {
            db.appDao().getAllProducts().collectLatest { products ->
                allProducts = products
                val productNames = allProducts.map { it.name }
                val searchAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, productNames)
                binding.etSearchProduct.setAdapter(searchAdapter)
            }
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

        val existingItem = saleItemsList.find { it.productId == product.id }
        if (existingItem != null) {
            val index = saleItemsList.indexOf(existingItem)
            saleItemsList[index] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            saleItemsList.add(SaleItem(productId = product.id, productName = product.name, quantity = 1, price = product.price))
        }
        
        adapter.updateList(saleItemsList)
        calculateTotals()
    }

    private fun calculateTotals() {
        val subtotal = saleItemsList.sumOf { it.price * it.quantity }
        val tax = subtotal * 0.10
        val total = subtotal + tax

        binding.tvSubtotal.text = "$${String.format("%.2f", subtotal)}"
        binding.tvTax.text = "$${String.format("%.2f", tax)}"
        binding.tvTotal.text = "$${String.format("%.2f", total)}"
    }

    private fun setupListeners() {
        binding.btnMenu.setOnClickListener { findNavController().navigateUp() }
        
        binding.btnFinalize.setOnClickListener {
            if (saleItemsList.isEmpty()) return@setOnClickListener
            finalizeSale()
        }
    }

    private fun finalizeSale() {
        val subtotal = saleItemsList.sumOf { it.price * it.quantity }
        val tax = subtotal * 0.10
        val total = subtotal + tax
        
        val sale = Sale(
            subtotal = subtotal,
            tax = tax,
            total = total,
            paymentMethod = "Efectivo"
        )

        lifecycleScope.launch {
            // Insert Sale and get ID
            val saleIdLong = db.appDao().insertSale(sale)
            val saleId = saleIdLong.toInt()

            // Prepare items with saleId
            val itemsToInsert = saleItemsList.map { it.copy(saleId = saleId) }
            db.appDao().insertSaleItems(itemsToInsert)

            // Update Stock
            saleItemsList.forEach { item ->
                val product = db.appDao().getProductById(item.productId)
                product?.let {
                    val updatedProduct = it.copy(stock = it.stock - item.quantity)
                    db.appDao().updateProduct(updatedProduct)
                }
            }

            Toast.makeText(context, "Venta realizada con éxito", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}