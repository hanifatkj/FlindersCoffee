package com.flinders.coffee.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.flinders.coffee.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val contact = binding.etLoginContact.text.toString().trim()
            val password = binding.etLoginPassword.text.toString().trim()

            if (contact.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Mohon masukkan email/no HP dan kata sandi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Selamat datang kembali, Rina!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        binding.tvToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Tautan reset kata sandi dikirim ke WhatsApp/email Anda.", Toast.LENGTH_SHORT).show()
        }

        binding.tvOpenOwner.setOnClickListener {
            Toast.makeText(this, "Masuk ke Mode Owner", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, com.flinders.coffee.ui.owner.OwnerMainActivity::class.java)
            startActivity(intent)
        }
    }
}
