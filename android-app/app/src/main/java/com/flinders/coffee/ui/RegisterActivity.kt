package com.flinders.coffee.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.flinders.coffee.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set default demo data
        binding.etFullName.setText("Rina")
        binding.etContact.setText("rina@email.com")
        binding.etPassword.setText("flinders2026")

        binding.btnRegister.setOnClickListener {
            val name = binding.etFullName.text.toString().trim()
            val contact = binding.etContact.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (name.isEmpty() || contact.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Mohon lengkapi seluruh kolom pendaftaran", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 8) {
                Toast.makeText(this, "Kata sandi minimal 8 karakter", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!binding.cbTerms.isChecked) {
                Toast.makeText(this, "Mohon setujui Syarat & Ketentuan Flinders Coffee", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Pendaftaran berhasil! Selamat datang di Flinders Coffee.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.tvToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
