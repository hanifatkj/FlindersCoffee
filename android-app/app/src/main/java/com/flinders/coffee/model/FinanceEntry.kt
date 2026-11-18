package com.flinders.coffee.model

data class FinanceEntry(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val amount: Long,
    val isExpense: Boolean
)
