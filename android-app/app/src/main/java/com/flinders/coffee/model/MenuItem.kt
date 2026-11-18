package com.flinders.coffee.model

/**
 * Model data Menu Flinders Coffee.
 * Memisahkan Harga Jual (yang dilihat kasir & pelanggan)
 * dan HPP / Biaya Produksi per porsi (yang digunakan database untuk menghitung laba bersih).
 */
data class MenuItem(
    val id: String,
    val name: String,
    val category: String,
    val price: Long,
    val hpp: Long,
    val isActive: Boolean = true,
    val description: String = "",
    val effectiveDate: String = ""
) {
    /**
     * Laba kotor / keuntungan bersih bahan per porsi
     */
    val profitPerPortion: Long
        get() = price - hpp

    /**
     * Persentase margin keuntungan
     */
    val marginPercentage: Double
        get() = if (price > 0) (profitPerPortion.toDouble() / price.toDouble()) * 100.0 else 0.0
}
