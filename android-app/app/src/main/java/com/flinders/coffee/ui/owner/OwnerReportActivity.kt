package com.flinders.coffee.ui.owner

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.flinders.coffee.R
import com.flinders.coffee.data.ApiClient
import com.flinders.coffee.data.FinanceRepository
import com.flinders.coffee.data.MenuRepository
import com.flinders.coffee.databinding.ActivityOwnerReportBinding
import com.flinders.coffee.model.FinanceEntry
import java.text.NumberFormat
import java.util.Locale

class OwnerReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOwnerReportBinding

    private val rupiahFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID")).apply {
        maximumFractionDigits = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOwnerReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnChangePeriod.setOnClickListener {
            Toast.makeText(this, "Periode aktif: November 2026", Toast.LENGTH_SHORT).show()
        }

        // Buka halaman Tambah / Kelola Menu & HPP
        binding.btnManageMenuProfit.setOnClickListener {
            startActivity(Intent(this, OwnerAddMenuActivity::class.java))
        }

        // Tambah Pengeluaran Operasional Asli (Simpan ke MySQL)
        binding.btnAddExpense.setOnClickListener {
            val name = binding.etExpenseName.text.toString().trim()
            val amountStr = binding.etExpenseAmount.text.toString().trim()

            if (name.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(this, "Ketik nama pengeluaran (misal: Beli gas, listrik) dan nominalnya", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountStr.toLongOrNull() ?: 0L
            if (amount <= 0) {
                Toast.makeText(this, "Nominal pengeluaran harus lebih dari 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Simpan langsung ke backend MySQL XAMPP via REST API
            ApiClient.addExpense("Operasional Toko", amount, name) { success, msg ->
                if (success) {
                    FinanceRepository.syncFromApi {
                        renderExpenseRows()
                        calculateTotals()
                    }
                    Toast.makeText(this, "Pengeluaran '$name' tersimpan ke database!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Gagal menyimpan ke server: $msg", Toast.LENGTH_SHORT).show()
                }
            }

            binding.etExpenseName.text?.clear()
            binding.etExpenseAmount.text?.clear()
            binding.etExpenseName.requestFocus()
        }

        // Konfirmasi & Simpan Laporan
        binding.btnConfirmReport.setOnClickListener {
            val totalSales = FinanceRepository.monthlySales
            val totalHpp = FinanceRepository.monthlyHpp
            val totalExpense = FinanceRepository.getTotalExpenses()
            val netProfit = FinanceRepository.getNetProfit()

            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Laporan Keuangan")
                .setMessage(
                    "Ringkasan Bulan November 2026:\n\n" +
                    "• Total Penjualan (Kasir): ${rupiahFormat.format(totalSales)}\n" +
                    "• HPP Bahan Penjualan: - ${rupiahFormat.format(totalHpp)}\n" +
                    "• Total Pengeluaran Tercatat: - ${rupiahFormat.format(totalExpense)}\n" +
                    "• Laba Bersih: ${rupiahFormat.format(netProfit)}\n\n" +
                    "Apakah Anda ingin mengonfirmasi dan menyimpan laporan ini?"
                )
                .setPositiveButton("Simpan Laporan") { _, _ ->
                    Toast.makeText(this, "Laporan Keuangan berhasil dikonfirmasi dan disimpan!", Toast.LENGTH_LONG).show()
                }
                .setNegativeButton("Cek Lagi", null)
                .show()
        }

        // Tutup Buku
        binding.btnCloseMonth.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Tutup Buku November 2026?")
                .setMessage("Tutup buku akan mengarsipkan seluruh catatan penjualan kasir, HPP, dan pengeluaran bulan ini ke riwayat permanen.")
                .setPositiveButton("Ya, Tutup Buku") { _, _ ->
                    Toast.makeText(this, "Buku bulan November 2026 telah resmi ditutup!", Toast.LENGTH_LONG).show()
                    finish()
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        renderExpenseRows()
        renderMenuProfitRows()
        calculateTotals()
        loadRealFinanceData()
    }

    override fun onResume() {
        super.onResume()
        renderMenuProfitRows()
        calculateTotals()
        loadRealFinanceData()
    }

    private fun loadRealFinanceData() {
        FinanceRepository.syncFromApi {
            renderExpenseRows()
            calculateTotals()
        }
    }

    private fun renderMenuProfitRows() {
        binding.containerMenuProfitRows.removeAllViews()
        val inflater = LayoutInflater.from(this)
        val menus = MenuRepository.getAllMenus()

        for (menu in menus) {
            val itemView = inflater.inflate(R.layout.item_menu_profit_row, binding.containerMenuProfitRows, false)
            val tvIcon = itemView.findViewById<TextView>(R.id.tvMenuCategoryIcon)
            val tvName = itemView.findViewById<TextView>(R.id.tvMenuName)
            val tvBreakdown = itemView.findViewById<TextView>(R.id.tvMenuPriceBreakdown)
            val tvProfit = itemView.findViewById<TextView>(R.id.tvMenuProfit)
            val tvMargin = itemView.findViewById<TextView>(R.id.tvMenuMargin)

            tvIcon.text = when (menu.category.lowercase(Locale.ROOT)) {
                "coffee", "kopi", "flavoured coffee" -> "☕"
                "non coffee", "nonkopi" -> "🍵"
                "tea" -> "🫖"
                "frappe" -> "🥤"
                "signature" -> "✨"
                "meal & snack", "makanan" -> "🥐"
                else -> "🍽️"
            }

            tvName.text = menu.name
            tvBreakdown.text = "Jual: ${rupiahFormat.format(menu.price)} · HPP: ${rupiahFormat.format(menu.hpp)}"
            tvProfit.text = "+ ${rupiahFormat.format(menu.profitPerPortion)}"
            tvMargin.text = "Margin ${String.format(Locale.US, "%.1f", menu.marginPercentage)}%"

            binding.containerMenuProfitRows.addView(itemView)
        }
    }

    private fun renderExpenseRows() {
        binding.containerExpenseRows.removeAllViews()
        val inflater = LayoutInflater.from(this)
        val expenses = FinanceRepository.getExpenses()

        for (entry in expenses) {
            val itemView = inflater.inflate(R.layout.item_finance_row, binding.containerExpenseRows, false)
            val tvName = itemView.findViewById<TextView>(R.id.tvEntryName)
            val tvAmount = itemView.findViewById<TextView>(R.id.tvEntryAmount)
            val btnDelete = itemView.findViewById<ImageButton>(R.id.btnDeleteEntry)

            tvName.text = entry.name
            tvAmount.text = rupiahFormat.format(entry.amount)
            tvAmount.setTextColor(ContextCompat.getColor(this, R.color.flinders_error))

            btnDelete.setOnClickListener {
                ApiClient.deleteExpense(entry.id) { success, _ ->
                    FinanceRepository.syncFromApi {
                        renderExpenseRows()
                        calculateTotals()
                    }
                }
                Toast.makeText(this, "'${entry.name}' dihapus", Toast.LENGTH_SHORT).show()
            }

            binding.containerExpenseRows.addView(itemView)
        }
    }

    private fun calculateTotals() {
        val totalSales = FinanceRepository.monthlySales
        val totalHpp = FinanceRepository.monthlyHpp
        val grossMargin = FinanceRepository.getGrossProfit()
        val totalExpense = FinanceRepository.getTotalExpenses()
        val netProfit = FinanceRepository.getNetProfit()

        binding.tvDbSalesAmount.text = rupiahFormat.format(totalSales)
        binding.tvDbHppAmount.text = "− " + rupiahFormat.format(totalHpp)
        binding.tvDbGrossMargin.text = rupiahFormat.format(grossMargin)

        binding.tvTotalExpense.text = rupiahFormat.format(totalExpense)

        binding.tvNetProfit.text = rupiahFormat.format(netProfit)
        binding.tvNetProfitFormula.text =
            "Penjualan ${rupiahFormat.format(totalSales)} − HPP ${rupiahFormat.format(totalHpp)} − Pengeluaran ${rupiahFormat.format(totalExpense)}"
    }
}
