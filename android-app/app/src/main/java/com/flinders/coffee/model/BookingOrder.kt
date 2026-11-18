package com.flinders.coffee.model

enum class BookingFilterStatus {
    ALL,
    ACTIVE,
    COMPLETED,
    CANCELLED
}

data class BookingOrder(
    val id: String,
    val customerName: String,
    val tableId: String,
    val roomName: String,
    val timeSlot: String,
    val guestCount: Int,
    var status: BookingFilterStatus
)
