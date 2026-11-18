package com.flinders.coffee.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.flinders.coffee.databinding.ActivityActiveBookingBinding

class ActiveBookingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityActiveBookingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActiveBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tableId = intent.getStringExtra("EXTRA_TABLE_ID") ?: "A04"
        val roomName = intent.getStringExtra("EXTRA_ROOM_NAME") ?: "Ruang A"
        val roomLocation = intent.getStringExtra("EXTRA_ROOM_LOCATION") ?: "Diluar depan kasir"
        val tableDesc = intent.getStringExtra("EXTRA_TABLE_DESC") ?: "Outdoor sudut depan kasir · 4 orang"

        binding.tvActiveTableBadge.text = tableId
        binding.tvActiveTableTitle.text = "$roomName · Meja ${tableId.drop(1)}"
        binding.tvActiveTableDesc.text = "$roomLocation · $tableDesc"

        binding.btnContactCashier.setOnClickListener {
            Toast.makeText(
                this,
                "Silakan tunjukkan kode BK017-$tableId-6K2 ke kasir untuk check-in kehadiran.",
                Toast.LENGTH_LONG
            ).show()
        }

        binding.btnCancelBooking.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Batalkan Booking?")
                .setMessage("Apakah Anda yakin ingin membatalkan booking meja $tableId (#BK-017)? Meja akan dilepas kembali.")
                .setPositiveButton("Ya, Batalkan") { _, _ ->
                    Toast.makeText(this, "Booking meja $tableId (#BK-017) telah dibatalkan.", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .setNegativeButton("Kembali", null)
                .show()
        }
    }
}
