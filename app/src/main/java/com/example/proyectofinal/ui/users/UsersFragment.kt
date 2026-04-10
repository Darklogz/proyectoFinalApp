package com.example.proyectofinal.ui.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectofinal.databinding.FragmentUsersBinding
import com.example.proyectofinal.models.User
import com.google.firebase.firestore.FirebaseFirestore

class UsersFragment : Fragment() {

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: UsersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()

        setupRecyclerView()
        fetchUsers()
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = UsersAdapter(emptyList(), 
            onEditClick = { user ->
                // Handle edit user
                Toast.makeText(context, "Editar: ${user.name}", Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = { user ->
                // Handle delete user
                deleteUser(user)
            }
        )
        binding.rvUsers.layoutManager = LinearLayoutManager(context)
        binding.rvUsers.adapter = adapter
    }

    private fun fetchUsers() {
        db.collection("users").addSnapshotListener { value, error ->
            if (error != null) return@addSnapshotListener
            val usersList = value?.toObjects(User::class.java) ?: emptyList()
            adapter.updateList(usersList)
        }
    }

    private fun deleteUser(user: User) {
        db.collection("users").document(user.id).delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Usuario eliminado", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupListeners() {
        binding.btnMenu.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnAddUser.setOnClickListener {
            // Logic to add user (can reuse register or a dialog)
            Toast.makeText(context, "Función para agregar usuario", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}