package com.flinders.coffee.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.flinders.coffee.model.FinanceEntry

/**
 * Service penghubung ke Firebase Firestore untuk Flinders Coffee.
 * Struktur Database Firestore:
 * - `finance_monthly` -> Dokumen bulanan yang diupdate dari web POS kasir laptop (totalSales & totalHpp).
 * - `expenses` -> Koleksi pengeluaran operasional (sewa, gaji, wifi, utilitas).
 * - `members` -> Koleksi data member, stamp, dan status voucher hadiah.
 * - `bookings` -> Koleksi reservasi meja pelanggan.
 */
object FirestoreService {

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private var financeListener: ListenerRegistration? = null
    private var expenseListener: ListenerRegistration? = null

    /**
     * Berlangganan data kalkulasi penjualan bulanan secara realtime dari Firestore.
     * Ketika kasir menyelesaikan transaksi di laptop, nilai totalSales & totalHpp
     * langsung otomatis terupdate di aplikasi Owner.
     */
    fun listenToMonthlyFinance(
        periodId: String = "2026_11",
        onUpdate: (sales: Long, hpp: Long) -> Unit,
        onError: ((Exception) -> Unit)? = null
    ) {
        try {
            financeListener?.remove()
            financeListener = firestore.collection("finance_monthly")
                .document(periodId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        onError?.invoke(error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val sales = snapshot.getLong("totalSales") ?: 48600000L
                        val hpp = snapshot.getLong("totalHpp") ?: 18900000L
                        FinanceRepository.updateSalesAndHpp(sales, hpp)
                        onUpdate(sales, hpp)
                    }
                }
        } catch (e: Exception) {
            onError?.invoke(e)
        }
    }

    /**
     * Simpan pengeluaran operasional baru ke Firestore.
     */
    fun saveExpense(
        name: String,
        amount: Long,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val expenseData = hashMapOf(
                "name" to name,
                "amount" to amount,
                "timestamp" to System.currentTimeMillis(),
                "author" to "Owner"
            )

            firestore.collection("expenses")
                .add(expenseData)
                .addOnSuccessListener { docRef ->
                    onSuccess(docRef.id)
                }
                .addOnFailureListener { e ->
                    onError(e)
                }
        } catch (e: Exception) {
            onError(e)
        }
    }

    /**
     * Simpan menu baru atau update menu ke Firestore katalog cloud.
     */
    fun saveMenu(
        item: com.flinders.coffee.model.MenuItem,
        onSuccess: (() -> Unit)? = null,
        onError: ((Exception) -> Unit)? = null
    ) {
        try {
            val menuData = hashMapOf(
                "id" to item.id,
                "name" to item.name,
                "category" to item.category,
                "price" to item.price,
                "hpp" to item.hpp,
                "isActive" to item.isActive,
                "description" to item.description,
                "effectiveDate" to item.effectiveDate,
                "updatedAt" to System.currentTimeMillis()
            )

            firestore.collection("menus")
                .document(item.id)
                .set(menuData)
                .addOnSuccessListener { onSuccess?.invoke() }
                .addOnFailureListener { e -> onError?.invoke(e) }
        } catch (e: Exception) {
            onError?.invoke(e)
        }
    }

    /**
     * Stop listener saat activity dihancurkan untuk menghemat memori.
     */
    fun detachListeners() {
        financeListener?.remove()
        financeListener = null
        expenseListener?.remove()
        expenseListener = null
    }
}
