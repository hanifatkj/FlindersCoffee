package com.flinders.coffee.ui.owner

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.flinders.coffee.databinding.ActivityOwnerProfileBinding

class OwnerProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOwnerProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOwnerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSaveProfile.setOnClickListener {
            Toast.makeText(this, "Profil Owner berhasil diperbarui", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
