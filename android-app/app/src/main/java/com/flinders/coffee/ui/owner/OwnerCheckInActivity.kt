package com.flinders.coffee.ui.owner

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.flinders.coffee.databinding.ActivityOwnerCheckInBinding

class OwnerCheckInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOwnerCheckInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOwnerCheckInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnConfirmCheckIn.setOnClickListener {
            Toast.makeText(this, "Check-in berhasil! Meja 3 sekarang berstatus 'Terisi'.", Toast.LENGTH_LONG).show()
            finish()
        }

        binding.btnCancelBooking.setOnClickListener {
            Toast.makeText(this, "Reservasi Meja 3 telah dibatalkan.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
