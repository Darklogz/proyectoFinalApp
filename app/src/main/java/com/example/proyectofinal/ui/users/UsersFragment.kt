package com.example.proyectofinal.ui.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectofinal.R
import com.example.proyectofinal.database.AppDatabase
import com.example.proyectofinal.databinding.FragmentUsersBinding
import com.example.proyectofinal.models.User
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UsersFragment : Fragment() {

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase
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
        db = AppDatabase.getDatabase(requireContext())

        setupRecyclerView()
        fetchUsers()
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = UsersAdapter(emptyList(), 
            onEditClick = { user ->
                Toast.makeText(context, "Editar: ${user.name}", Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = { user ->
                deleteUser(user)
            }
        )
        binding.rvUsers.layoutManager = LinearLayoutManager(context)
        binding.rvUsers.adapter = adapter
    }

    private fun fetchUsers() {
        lifecycleScope.launch {
            db.appDao().getAllUsers().collectLatest { usersList ->
                adapter.updateList(usersList)
            }
        }
    }

    private fun deleteUser(user: User) {
        lifecycleScope.launch {
            db.appDao().deleteUser(user)
            Toast.makeText(context, "Usuario eliminado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupListeners() {
        binding.btnMenu.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnAddUser.setOnClickListener {
            findNavController().navigate(R.id.action_usersFragment_to_addUserFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}