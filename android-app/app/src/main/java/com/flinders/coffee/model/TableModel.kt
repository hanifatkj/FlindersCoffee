package com.flinders.coffee.model

enum class TableStatus {
    AVAILABLE,
    BOOKED,
    OCCUPIED
}

data class TableItem(
    val id: String,          // e.g. "A01", "B03", "C06"
    val roomCode: String,    // "A", "B", "C"
    val number: String,      // "01", "02", ...
    val capacity: Int,       // 2, 4, etc.
    val description: String, // e.g. "Depan kasir · 4 orang"
    var status: TableStatus
)

data class RoomInfo(
    val code: String,        // "A", "B", "C"
    val name: String,        // "Ruang A"
    val location: String,    // "Diluar depan kasir"
    val tables: List<TableItem>
)
