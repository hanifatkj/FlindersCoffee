package com.flinders.coffee.data

import com.flinders.coffee.model.FinanceEntry

/**
 * Repository data keuangan untuk Flinders Coffee.
 * - Terhubung langsung ke API MySQL XAMPP (flinders-api).
 * - Menghitung Penjualan Asli, HPP Asli, Pengeluaran Asli, dan Laba Bersih secara realtime.
 * - Tidak lagi menggunakan angka dummy; murni hasil transaksi kasir & operasional.
 */
object FinanceRepository {

    // Total akumulasi penjualan asli dari transaksi kasir/POS di MySQL (default 0L)
    var monthlySales: Long = 0L
        private set

    // Total Harga Pokok Penjualan (HPP) bahan baku asli dari transaksi di MySQL (default 0L)
    var monthlyHpp: Long = 0L
        private set

    // Daftar pengeluaran operasional asli dari tabel expenses MySQL
    private val expenseItems = mutableListOf<FinanceEntry>()

    fun getExpenses(): List<FinanceEntry> = expenseItems.toList()

    fun addExpense(name: String, amount: Long, id: Long = System.currentTimeMillis()): FinanceEntry {
        val entry = FinanceEntry(id = id, name = name, amount = amount, isExpense = true)
        expenseItems.add(0, entry)
        return entry
    }

    fun removeExpense(index: Int) {
        if (index in 0 until expenseItems.size) {
            expenseItems.removeAt(index)
        }
    }

    fun getTotalExpenses(): Long = expenseItems.sumOf { it.amount }

    fun getGrossProfit(): Long = monthlySales - monthlyHpp

    fun getNetProfit(): Long = monthlySales - monthlyHpp - getTotalExpenses()

    fun updateSalesAndHpp(sales: Long, hpp: Long) {
        monthlySales = sales
        monthlyHpp = hpp
    }

    /**
     * Sinkronisasi data finansial asli secara realtime dari database MySQL XAMPP
     */
    fun syncFromApi(period: String? = null, onComplete: (() -> Unit)? = null) {
        ApiClient.getSummary(period) { success, data, _ ->
            if (success && data != null) {
                monthlySales = data.optLong("total_sales", 0L)
                monthlyHpp = data.optLong("total_hpp", 0L)
            }
            // Muat daftar pengeluaran asli dari tabel expenses MySQL
            ApiClient.getExpenses(period) { expSuccess, expList, _ ->
                if (expSuccess && expList != null) {
                    expenseItems.clear()
                    expenseItems.addAll(expList)
                }
                onComplete?.invoke()
            }
        }
    }
}
