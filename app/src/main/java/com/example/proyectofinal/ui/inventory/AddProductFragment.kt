package com.example.proyectofinal.ui.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.proyectofinal.database.AppDatabase
import com.example.proyectofinal.databinding.FragmentAddProductBinding
import com.example.proyectofinal.models.Product
import kotlinx.coroutines.launch

class AddProductFragment : Fragment() {

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getDatabase(requireContext())

        setupCategorySpinner()

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.btnCancel.setOnClickListener { findNavController().navigateUp() }
        
        binding.btnSave.setOnClickListener {
            saveProduct()
        }
    }

    private fun setupCategorySpinner() {
        val categories = arrayOf("Electrónica", "Ropa", "Alimentos", "Hogar", "Otros")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        binding.actvCategory.setAdapter(adapter)
    }

    private fun saveProduct() {
        val name = binding.etProductName.text.toString().trim()
        val price = binding.etPrice.text.toString().toDoubleOrNull() ?: 0.0
        val stock = binding.etStock.text.toString().toIntOrNull() ?: 0
        val sku = binding.etSku.text.toString().trim()
        val minStock = binding.etMinStock.text.toString().toIntOrNull() ?: 0
        val category = binding.actvCategory.text.toString()

        if (name.isEmpty() || sku.isEmpty()) {
            Toast.makeText(context, "Nombre y SKU son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val product = Product(
            name = name,
            price = price,
            stock = stock,
            sku = sku,
            minStock = minStock,
            category = category
        )

        lifecycleScope.launch {
            db.appDao().insertProduct(product)
            Toast.makeText(context, "Producto guardado", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}