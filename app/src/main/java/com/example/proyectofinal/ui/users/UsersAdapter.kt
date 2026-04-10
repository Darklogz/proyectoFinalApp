package com.example.proyectofinal.ui.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.databinding.ItemUserBinding
import com.example.proyectofinal.models.User

class UsersAdapter(
    private var users: List<User>,
    private val onEditClick: (User) -> Unit,
    private val onDeleteClick: (User) -> Unit
) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.binding.tvUserName.text = user.name
        holder.binding.tvUserEmail.text = user.email
        holder.binding.tvUserRole.text = user.role
        holder.binding.tvUserStatus.text = if (user.active) "Activo" else "Inactivo"
        
        holder.binding.tvUserStatus.setBackgroundResource(
            if (user.active) R.drawable.bg_badge_green else R.drawable.bg_badge_red
        )

        holder.binding.btnEdit.setOnClickListener { onEditClick(user) }
        holder.binding.btnDelete.setOnClickListener { onDeleteClick(user) }
    }

    override fun getItemCount() = users.size

    fun updateList(newList: List<User>) {
        users = newList
        notifyDataSetChanged()
    }
}