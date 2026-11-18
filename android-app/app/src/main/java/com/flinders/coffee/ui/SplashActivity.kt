package com.flinders.coffee.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.flinders.coffee.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Animasi fade in sederhana pada logo
        binding.ivSplashLogo.alpha = 0f
        binding.tvSplashBrand.alpha = 0f
        binding.ivSplashLogo.animate().alpha(1f).setDuration(800).start()
        binding.tvSplashBrand.animate().alpha(1f).setDuration(1000).start()

        Handler(Looper.getMainLooper()).postDelayed({
            val prefs = getSharedPreferences("flinders_prefs", Context.MODE_PRIVATE)
            val isOnboardingCompleted = prefs.getBoolean("onboarding_completed", false)

            val nextIntent = if (!isOnboardingCompleted) {
                Intent(this, OnboardingActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }

            startActivity(nextIntent)
            finish()
        }, 1800)
    }
}
