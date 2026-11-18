package com.flinders.coffee.data

import com.flinders.coffee.model.RoomInfo
import com.flinders.coffee.model.TableItem
import com.flinders.coffee.model.TableStatus

/**
 * Repository Meja Flinders Coffee.
 * - 18 Meja terbagi dalam Ruang A, Ruang B, dan Ruang C.
 * - Terhubung langsung ke API MySQL XAMPP (tabel `tables`).
 * - Status realtime: AVAILABLE, OCCUPIED, BOOKED.
 */
object TableRepository {

    val rooms: List<RoomInfo> = listOf(
        // Ruang A (Diluar depan kasir) - 6 Meja
        RoomInfo(
            code = "A",
            name = "Ruang A",
            location = "Diluar depan kasir",
            tables = listOf(
                TableItem("A01", "A", "01", 2, "Outdoor depan kasir · 2 orang", TableStatus.AVAILABLE),
                TableItem("A02", "A", "02", 4, "Outdoor depan kasir · 4 orang", TableStatus.AVAILABLE),
                TableItem("A03", "A", "03", 4, "Outdoor smoking area · 4 orang", TableStatus.AVAILABLE),
                TableItem("A04", "A", "04", 4, "Outdoor sudut depan kasir · 4 orang", TableStatus.AVAILABLE),
                TableItem("A05", "A", "05", 2, "Outdoor samping kasir · 2 orang", TableStatus.AVAILABLE),
                TableItem("A06", "A", "06", 4, "Outdoor samping taman · 4 orang", TableStatus.AVAILABLE)
            )
        ),
        // Ruang B (Didalam bagian tengah) - 6 Meja
        RoomInfo(
            code = "B",
            name = "Ruang B",
            location = "Didalam bagian tengah",
            tables = listOf(
                TableItem("B01", "B", "01", 4, "Indoor tengah sofa · 4 orang", TableStatus.AVAILABLE),
                TableItem("B02", "B", "02", 4, "Indoor tengah dekat bar · 4 orang", TableStatus.AVAILABLE),
                TableItem("B03", "B", "03", 2, "Indoor tengah meja bundar · 2 orang", TableStatus.AVAILABLE),
                TableItem("B04", "B", "04", 4, "Indoor tengah AC dingin · 4 orang", TableStatus.AVAILABLE),
                TableItem("B05", "B", "05", 6, "Indoor tengah meja panjang · 6 orang", TableStatus.AVAILABLE),
                TableItem("B06", "B", "06", 4, "Indoor tengah sudut colokan · 4 orang", TableStatus.AVAILABLE)
            )
        ),
        // Ruang C (Didekat WC / dibelakang) - 6 Meja
        RoomInfo(
            code = "C",
            name = "Ruang C",
            location = "Didekat WC / dibelakang",
            tables = listOf(
                TableItem("C01", "C", "01", 2, "Belakang dekat taman · 2 orang", TableStatus.AVAILABLE),
                TableItem("C02", "C", "02", 4, "Belakang semi-outdoor · 4 orang", TableStatus.AVAILABLE),
                TableItem("C03", "C", "03", 2, "Belakang lorong WC · 2 orang", TableStatus.AVAILABLE),
                TableItem("C04", "C", "04", 4, "Belakang sudut tenang · 4 orang", TableStatus.AVAILABLE),
                TableItem("C05", "C", "05", 4, "Belakang dekat kolam · 4 orang", TableStatus.AVAILABLE),
                TableItem("C06", "C", "06", 4, "Belakang area santai · 4 orang", TableStatus.AVAILABLE)
            )
        )
    )

    fun getAllTables(): List<TableItem> {
        return rooms.flatMap { it.tables }
    }

    fun updateTableStatus(tableId: String, newStatus: TableStatus): Boolean {
        for (room in rooms) {
            for (table in room.tables) {
                if (table.id.equals(tableId, ignoreCase = true)) {
                    table.status = newStatus
                    return true
                }
            }
        }
        return false
    }

    fun getRoom(code: String): RoomInfo? {
        return rooms.find { it.code.equals(code, ignoreCase = true) }
    }

    fun getTable(tableId: String): TableItem? {
        return getAllTables().find { it.id.equals(tableId, ignoreCase = true) }
    }

    fun getTotalTablesCount(): Int = getAllTables().size

    fun getBookedOrOccupiedCount(): Int =
        getAllTables().count { it.status == TableStatus.BOOKED || it.status == TableStatus.OCCUPIED }

    fun getAvailableCount(): Int =
        getAllTables().count { it.status == TableStatus.AVAILABLE }

    /**
     * Sinkronisasi status 18 meja dari database MySQL XAMPP
     */
    fun syncFromApi(onComplete: (() -> Unit)? = null) {
        ApiClient.getTables { success, jsonArray, _ ->
            if (success && jsonArray != null) {
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val tableId = obj.optString("id")
                    val statusStr = obj.optString("status", "AVAILABLE")
                    val status = when (statusStr.uppercase()) {
                        "OCCUPIED" -> TableStatus.OCCUPIED
                        "BOOKED" -> TableStatus.BOOKED
                        else -> TableStatus.AVAILABLE
                    }
                    updateTableStatus(tableId, status)
                }
            }
            onComplete?.invoke()
        }
    }
}
