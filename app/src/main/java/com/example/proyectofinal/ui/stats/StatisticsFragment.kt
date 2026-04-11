package com.example.proyectofinal.ui.stats

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.proyectofinal.R
import com.example.proyectofinal.database.AppDatabase
import com.example.proyectofinal.databinding.FragmentStatisticsBinding
import com.example.proyectofinal.models.Sale
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = AppDatabase.getDatabase(requireContext())

        setupListeners()
        fetchStats()
    }

    private fun setupListeners() {
        binding.btnMenu.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun fetchStats() {
        binding.statTotalRevenue.tvLabel.text = "Ventas Totales"
        binding.statTotalRevenue.ivIcon.setImageResource(R.drawable.ic_sales)
        
        binding.statTotalUnits.tvLabel.text = "Unidades Vendidas"
        binding.statTotalUnits.ivIcon.setImageResource(R.drawable.ic_inventory)

        viewLifecycleOwner.lifecycleScope.launch {
            db.appDao().getAllSales().collectLatest { sales ->
                if (_binding == null) return@collectLatest
                
                val totalRevenue = sales.sumOf { it.total }
                var unitsSold = 0
                sales.forEach { sale ->
                    val items = db.appDao().getItemsForSale(sale.id)
                    unitsSold += items.sumOf { it.quantity }
                }

                binding.statTotalRevenue.tvValue.text = "$${String.format("%.1fk", totalRevenue / 1000)}"
                binding.statTotalUnits.tvValue.text = unitsSold.toString()
                
                setupChart(sales)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            db.appDao().getLowStockProducts().collectLatest { products ->
                if (_binding == null) return@collectLatest
                val lowStockText = products.take(3).joinToString("\n") { "${it.name} (${it.stock})" }
                binding.tvTopProducts.text = if (lowStockText.isEmpty()) "Sin alertas de stock" else "Críticos:\n$lowStockText"
            }
        }
    }

    private fun setupChart(sales: List<Sale>) {
        val entries = mutableListOf<Entry>()
        
        // Group sales by day (last 7 days)
        val salesByDay = sales.groupBy { 
            val sdf = SimpleDateFormat("dd", Locale.getDefault())
            sdf.format(Date(it.timestamp))
        }.mapValues { entry -> entry.value.sumOf { it.total } }

        var index = 0f
        salesByDay.keys.sorted().forEach { day ->
            entries.add(Entry(index++, salesByDay[day]?.toFloat() ?: 0f))
        }

        val dataSet = LineDataSet(entries, "Ventas")
        dataSet.color = Color.BLUE
        dataSet.valueTextColor = Color.BLACK
        dataSet.setCircleColor(Color.BLUE)
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 4f
        dataSet.setDrawCircleHole(false)
        dataSet.valueTextSize = 10f
        dataSet.setDrawFilled(true)
        dataSet.fillColor = Color.LTGRAY

        val lineData = LineData(dataSet)
        binding.lineChart.data = lineData
        binding.lineChart.description.isEnabled = false
        binding.lineChart.animateX(1000)
        binding.lineChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}