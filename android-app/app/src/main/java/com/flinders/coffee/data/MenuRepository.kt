package com.flinders.coffee.data

import com.flinders.coffee.model.MenuItem

/**
 * Repository Menu Flinders Coffee.
 * Menyimpan data menu autentik dari daftar menu fisik meja Flinders Coffee
 * lengkap dengan Harga Jual dan HPP Produksi per porsi.
 */
object MenuRepository {

    private val menuList = mutableListOf(
        // 1. COFFEE (ICED 🔵 / HOT 🔴)
        MenuItem("MN-01", "Americano", "Coffee", 18000L, 6500L, true, "Espresso klasik dengan air mineral segar (Dingin 🔵 / Panas 🔴)", "17 Nov 2026"),
        MenuItem("MN-02", "Kopi Susu", "Coffee", 18000L, 6500L, true, "Espresso double shot dengan creamy milk & aren murni (Dingin 🔵 / Panas 🔴)", "17 Nov 2026"),
        MenuItem("MN-03", "Flat White", "Coffee", 20000L, 7500L, true, "Espresso velvety dengan microfoam steamed milk (Dingin 🔵 / Panas 🔴)", "17 Nov 2026"),
        MenuItem("MN-04", "Cafe Latte", "Coffee", 20000L, 7500L, true, "Espresso lembut berpadu steamed milk halus berbusa (Dingin 🔵 / Panas 🔴)", "17 Nov 2026"),
        MenuItem("MN-05", "Cappucino", "Coffee", 20000L, 7500L, true, "Espresso kaya rasa dengan foam tebal gurih berpadu (Dingin 🔵 / Panas 🔴)", "17 Nov 2026"),

        // 2. FLAVOURED COFFEE (ICED 🔵 / HOT 🔴)
        MenuItem("MN-06", "Butterscotch Latte", "Flavoured Coffee", 22000L, 8000L, true, "Espresso berpadu sirup butterscotch gurih karamel (Dingin 🔵 / Panas 🔴)", "17 Nov 2026"),
        MenuItem("MN-07", "Vanilla Latte", "Flavoured Coffee", 22000L, 8000L, true, "Espresso harum ekstrak vanila manis lembut (Dingin 🔵 / Panas 🔴)", "17 Nov 2026"),
        MenuItem("MN-08", "Hazelnut Latte", "Flavoured Coffee", 22000L, 8000L, true, "Espresso beraroma hazelnut panggang gurih (Dingin 🔵 / Panas 🔴)", "17 Nov 2026"),
        MenuItem("MN-09", "Pandan Latte", "Flavoured Coffee", 22000L, 8000L, true, "Espresso dengan aroma wangi pandan alami (Dingin 🔵 / Panas 🔴)", "17 Nov 2026"),
        MenuItem("MN-10", "Chococino", "Flavoured Coffee", 22000L, 8500L, true, "Paduan kopi moka espresso dan cokelat nikmat (Dingin 🔵 / Panas 🔴)", "17 Nov 2026"),
        MenuItem("MN-11", "Caramel Macchiato", "Flavoured Coffee", 22000L, 8500L, true, "Espresso, susu, sirup vanilla & saus karamel lezat (Dingin 🔵 / Panas 🔴)", "17 Nov 2026"),

        // 3. SIGNATURE
        MenuItem("MN-12", "Kopi Nera (Gula Aren)", "Signature", 20000L, 7500L, true, "❤️❤️ Signature kopi aren khas Flinders racikan spesial (Dingin 🔵 / Panas 🔴)", "17 Nov 2026"),
        MenuItem("MN-13", "Creamy Butterscotch", "Signature", 23000L, 9000L, true, "Minuman khas butterscotch creamy kaya tekstur dan manis pas", "17 Nov 2026"),
        MenuItem("MN-14", "Butterscotch Seasalt", "Signature", 23000L, 9000L, true, "Perpaduan manis gurih butterscotch dengan sentuhan seasalt seimbang", "17 Nov 2026"),
        MenuItem("MN-15", "Mont Blanc", "Signature", 23000L, 9000L, true, "Minuman dingin signature lembut dengan rasa dessert pegunungan salju", "17 Nov 2026"),
        MenuItem("MN-16", "Choco Hazelnut", "Signature", 22000L, 8500L, true, "Cokelat pekat berpadu kacang hazelnut gurih nikmat", "17 Nov 2026"),
        MenuItem("MN-17", "Choco Berry", "Signature", 22000L, 8500L, true, "Kombinasi kaya rasa cokelat lumer dan kesegaran rasa berry", "17 Nov 2026"),
        MenuItem("MN-18", "Creamy Lotus", "Signature", 22000L, 8500L, true, "Minuman creamy biskuit Lotus Biscoff legit renyah", "17 Nov 2026"),
        MenuItem("MN-19", "Crunchy Caramel", "Signature", 23000L, 9000L, true, "❤️ Karamel manis legit dengan sensasi renyah kriuk istimewa", "17 Nov 2026"),

        // 4. AMERICANO SERIES
        MenuItem("MN-20", "Blueberry Americano", "Americano Series", 20000L, 7000L, true, "❤️ Sensasi Americano segar berpadu buah blueberry manis-asam", "17 Nov 2026"),
        MenuItem("MN-21", "Peach Americano", "Americano Series", 20000L, 7000L, true, "Americano dingin menyegarkan dengan aroma dan rasa buah peach", "17 Nov 2026"),
        MenuItem("MN-22", "Passion Fruit Americano", "Americano Series", 20000L, 7000L, true, "Americano tropis dengan kesegaran markisa alami", "17 Nov 2026"),

        // 5. NON COFFEE
        MenuItem("MN-23", "Matcha Latte", "Non Coffee", 22000L, 8500L, true, "Matcha murni Uji Jepang dengan susu segar lembut (Dingin 🔵 / Panas 🔴)", "17 Nov 2026"),
        MenuItem("MN-24", "Chocolate", "Non Coffee", 20000L, 7500L, true, "❤️❤️ Cokelat Belgia pekat berpadu fresh milk (Dingin 🔵 / Panas 🔴)", "17 Nov 2026"),
        MenuItem("MN-25", "Cookies & Cream", "Non Coffee", 20000L, 7500L, true, "Minuman susu manis lembut dengan remahan biskuit cokelat gurih", "17 Nov 2026"),
        MenuItem("MN-26", "Choco Nutella", "Non Coffee", 20000L, 8000L, true, "Cokelat susu dengan selai hazelnut cokelat Nutella asli", "17 Nov 2026"),
        MenuItem("MN-27", "Red Velvet", "Non Coffee", 20000L, 7500L, true, "Minuman red velvet lembut beraroma kue manis legit (Dingin 🔵 / Panas 🔴)", "17 Nov 2026"),
        MenuItem("MN-28", "Vanilla", "Non Coffee", 20000L, 7000L, true, "Minuman susu vanila klasik lembut yang menenangkan (Dingin 🔵 / Panas 🔴)", "17 Nov 2026"),
        MenuItem("MN-29", "Fruity Milkshake", "Non Coffee", 20000L, 7500L, true, "Milkshake buah segar lembut dan creamy berbusa halus", "17 Nov 2026"),
        MenuItem("MN-30", "Fruity Yogurt", "Non Coffee", 18000L, 6500L, true, "❤️ Yogurt segar pilihan: Blueberry, Strawberry, Lychee, Mango, Passion Fruit", "17 Nov 2026"),

        // 6. FRAPPE
        MenuItem("MN-31", "Chocolate Frappe", "Frappe", 23000L, 9000L, true, "Blended es cokelat kental dan creamy bertabur garnish", "17 Nov 2026"),
        MenuItem("MN-32", "Red Velvet Frappe", "Frappe", 23000L, 9000L, true, "Frappe red velvet dingin pekat bertabur topping lembut", "17 Nov 2026"),
        MenuItem("MN-33", "Matcha Frappe", "Frappe", 23000L, 9000L, true, "Blended matcha Jepang dingin kaya rasa susu segar", "17 Nov 2026"),
        MenuItem("MN-34", "Vanilla Frappe", "Frappe", 23000L, 9000L, true, "Frappe vanila manis dingin segar dengan tekstur halus", "17 Nov 2026"),
        MenuItem("MN-35", "Flinderscino", "Frappe", 23000L, 9000L, true, "Signature ice-blended khas Flinders Coffee kaya cita rasa", "17 Nov 2026"),

        // 7. TEA (ICED 🔵 / HOT 🔴)
        MenuItem("MN-36", "Original Tea", "Tea", 15000L, 4500L, true, "Seduhan teh murni berkualitas harum dan menyegarkan (Dingin 🔵 / Panas 🔴)", "17 Nov 2026"),
        MenuItem("MN-37", "Milk Tea", "Tea", 18000L, 6000L, true, "Teh aromatik dipadukan dengan susu kental gurih (Dingin 🔵 / Panas 🔴)", "17 Nov 2026"),
        MenuItem("MN-38", "Thai Tea", "Tea", 18000L, 6000L, true, "Teh Thailand otentik dengan aroma rempah manis creamy (Dingin 🔵 / Panas 🔴)", "17 Nov 2026"),
        MenuItem("MN-39", "Green Tea", "Tea", 18000L, 6000L, true, "Teh hijau segar aromatik kaya antioksidan (Dingin 🔵 / Panas 🔴)", "17 Nov 2026"),
        MenuItem("MN-40", "Lemon Tea", "Tea", 18000L, 6000L, true, "Teh segar berpadu sari jeruk lemon asli penghilang dahaga (Dingin 🔵 / Panas 🔴)", "17 Nov 2026"),
        MenuItem("MN-41", "Lychee Tea", "Tea", 18000L, 6500L, true, "Teh melati harum disajikan dengan buah leci segar (Dingin 🔵 / Panas 🔴)", "17 Nov 2026"),

        // 8. MEAL & SNACK
        MenuItem("MN-42", "Nasi Ayam", "Meal & Snack", 25000L, 12000L, true, "Nasi putih hangat dengan olahan ayam bumbu rempah gurih lezat", "17 Nov 2026"),
        MenuItem("MN-43", "Beef Burger Ori", "Meal & Snack", 25000L, 12000L, true, "Burger roti panggang empuk isi patty daging sapi gurih & sayuran segar", "17 Nov 2026"),
        MenuItem("MN-44", "Mix Platter", "Meal & Snack", 20000L, 9500L, true, "Paket camilan lengkap aneka gorengan gurih renyah favorit kumpul", "17 Nov 2026"),
        MenuItem("MN-45", "Mie Goreng / Rebus", "Meal & Snack", 15000L, 6000L, true, "Mie lezat pilihan (Goreng / Rebus) disajikan hangat menggoda", "17 Nov 2026"),
        MenuItem("MN-46", "Kentang Goreng", "Meal & Snack", 15000L, 5500L, true, "Kentang goreng renyah bumbu gurih lezat renyah di luar lembut di dalam", "17 Nov 2026"),
        MenuItem("MN-47", "Pisang Goreng Ori", "Meal & Snack", 15000L, 5500L, true, "Pisang goreng renyah manis alami cocok dinikmati bersama kopi", "17 Nov 2026"),
        MenuItem("MN-48", "Bakpao Mini", "Meal & Snack", 10000L, 3500L, true, "Kukusan bakpao mini empuk lembut beraneka isian manis lezat", "17 Nov 2026"),
        MenuItem("MN-49", "Roti Bakar", "Meal & Snack", 10000L, 3500L, true, "Roti bakar mentega pilihan: Coklat, Keju, Blueberry, Strawberry", "17 Nov 2026")
    )

    fun getAllMenus(): List<MenuItem> = menuList.toList()

    fun getActiveMenus(): List<MenuItem> = menuList.filter { it.isActive }

    fun addMenu(item: MenuItem) {
        menuList.add(0, item) // Taruh di paling atas agar menu baru langsung terlihat
    }

    fun toggleStatus(id: String): Boolean {
        val index = menuList.indexOfFirst { it.id == id }
        if (index != -1) {
            val item = menuList[index]
            val updated = item.copy(isActive = !item.isActive)
            menuList[index] = updated
            return updated.isActive
        }
        return false
    }

    fun importMenus(items: List<MenuItem>): Int {
        var count = 0
        for (newItem in items) {
            val existingIndex = menuList.indexOfFirst { it.name.equals(newItem.name, ignoreCase = true) }
            if (existingIndex != -1) {
                menuList[existingIndex] = newItem
            } else {
                menuList.add(0, newItem)
            }
            count++
        }
        return count
    }

    fun filterMenus(query: String, category: String, isActive: Boolean?): List<MenuItem> {
        return menuList.filter { item ->
            val matchQuery = query.isEmpty() || item.name.contains(query, ignoreCase = true) || item.description.contains(query, ignoreCase = true)
            val matchCategory = category.equals("Semua", ignoreCase = true) ||
                    item.category.equals(category, ignoreCase = true) ||
                    (category.equals("Kopi", ignoreCase = true) && (item.category.contains("Coffee", ignoreCase = true) || item.category.contains("Kopi", ignoreCase = true))) ||
                    (category.equals("Nonkopi", ignoreCase = true) && (item.category.contains("Non", ignoreCase = true) || item.category.equals("Tea", ignoreCase = true) || item.category.equals("Frappe", ignoreCase = true))) ||
                    (category.equals("Makanan", ignoreCase = true) && (item.category.contains("Meal", ignoreCase = true) || item.category.contains("Snack", ignoreCase = true) || item.category.equals("Makanan", ignoreCase = true)))
            val matchStatus = isActive == null || item.isActive == isActive
            matchQuery && matchCategory && matchStatus
        }
    }

    fun getTotalEstimatedProfitPerServing(): Long {
        return menuList.sumOf { it.profitPerPortion }
    }
}
