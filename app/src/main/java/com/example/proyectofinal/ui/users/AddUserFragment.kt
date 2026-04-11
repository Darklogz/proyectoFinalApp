package com.example.proyectofinal.ui.users

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
import com.example.proyectofinal.databinding.FragmentAddUserBinding
import com.example.proyectofinal.models.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AddUserFragment : Fragment() {

    private var _binding: FragmentAddUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = AppDatabase.getDatabase(requireContext())

        setupRoleSpinner()

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        
        binding.btnSaveUser.setOnClickListener {
            registerUser()
        }
    }

    private fun setupRoleSpinner() {
        val roles = arrayOf("Administrador", "Vendedor", "Almacén")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, roles)
        binding.actvRole.setAdapter(adapter)
    }

    private fun registerUser() {
        val name = binding.etUserName.text.toString().trim()
        val email = binding.etUserEmail.text.toString().trim()
        val password = binding.etUserPassword.text.toString().trim()
        val role = binding.actvRole.text.toString()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || role.isEmpty()) {
            Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        // Creating user in Firebase
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUid = task.result?.user?.uid ?: ""
                    val newUser = User(firebaseUid = firebaseUid, name = name, email = email, role = role)
                    
                    lifecycleScope.launch {
                        db.appDao().insertUser(newUser)
                        Toast.makeText(context, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                } else {
                    Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}