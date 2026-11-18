package com.flinders.coffee.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.flinders.coffee.R
import com.flinders.coffee.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private var currentStep = 0

    private val slides = listOf(
        SlideItem(
            iconRes = R.drawable.ic_flinders_logo,
            title = "Reservasi Meja Instan",
            description = "Pilih meja favorit di Ruang A, B, atau C tanpa antre. Nikmati suasana kerja dan santai yang nyaman di Flinders Coffee."
        ),
        SlideItem(
            iconRes = R.drawable.ic_gift,
            title = "Kumpulkan Stamp & Hadiah",
            description = "Setiap pesanan di kasir memberikan 1 stamp harian. Kumpulkan hingga 10 stamp dan tukarkan dengan 1 minuman gratis pilihan Anda."
        ),
        SlideItem(
            iconRes = R.drawable.ic_qr_code,
            title = "Terhubung Bersama Flinders",
            description = "Tunjukkan kartu member digital, nikmati kemudahan reservasi, dan pantau promo eksklusif langsung dari HP Anda."
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        renderCurrentSlide()

        binding.btnNextSlide.setOnClickListener {
            if (currentStep < slides.size - 1) {
                currentStep++
                renderCurrentSlide()
            } else {
                completeOnboarding()
            }
        }

        binding.btnSkipOnboarding.setOnClickListener {
            completeOnboarding()
        }
    }

    private fun renderCurrentSlide() {
        val slide = slides[currentStep]

        binding.ivSlideIllustration.setImageResource(slide.iconRes)
        binding.tvSlideTitle.text = slide.title
        binding.tvSlideDescription.text = slide.description

        if (currentStep == slides.size - 1) {
            binding.btnNextSlide.text = "Mulai Sekarang"
            binding.btnSkipOnboarding.visibility = android.view.View.INVISIBLE
        } else {
            binding.btnNextSlide.text = "Lanjut"
            binding.btnSkipOnboarding.visibility = android.view.View.VISIBLE
        }

        updateIndicators(currentStep)
    }

    private fun updateIndicators(position: Int) {
        val activeRes = R.drawable.bg_dot_active
        val inactiveRes = R.drawable.bg_dot_inactive

        val dot1Params = binding.dot1.layoutParams
        val dot2Params = binding.dot2.layoutParams
        val dot3Params = binding.dot3.layoutParams

        val density = resources.displayMetrics.density
        val activeWidth = (24 * density).toInt()
        val inactiveWidth = (8 * density).toInt()

        when (position) {
            0 -> {
                binding.dot1.setBackgroundResource(activeRes)
                binding.dot2.setBackgroundResource(inactiveRes)
                binding.dot3.setBackgroundResource(inactiveRes)
                dot1Params.width = activeWidth
                dot2Params.width = inactiveWidth
                dot3Params.width = inactiveWidth
            }
            1 -> {
                binding.dot1.setBackgroundResource(inactiveRes)
                binding.dot2.setBackgroundResource(activeRes)
                binding.dot3.setBackgroundResource(inactiveRes)
                dot1Params.width = inactiveWidth
                dot2Params.width = activeWidth
                dot3Params.width = inactiveWidth
            }
            2 -> {
                binding.dot1.setBackgroundResource(inactiveRes)
                binding.dot2.setBackgroundResource(inactiveRes)
                binding.dot3.setBackgroundResource(activeRes)
                dot1Params.width = inactiveWidth
                dot2Params.width = inactiveWidth
                dot3Params.width = activeWidth
            }
        }
        binding.dot1.layoutParams = dot1Params
        binding.dot2.layoutParams = dot2Params
        binding.dot3.layoutParams = dot3Params
    }

    private fun completeOnboarding() {
        val prefs = getSharedPreferences("flinders_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("onboarding_completed", true).apply()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private data class SlideItem(
        val iconRes: Int,
        val title: String,
        val description: String
    )
}
