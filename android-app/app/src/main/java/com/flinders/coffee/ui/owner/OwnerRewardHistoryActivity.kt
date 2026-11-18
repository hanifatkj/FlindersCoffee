package com.flinders.coffee.ui.owner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.flinders.coffee.databinding.ActivityOwnerRewardHistoryBinding

class OwnerRewardHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOwnerRewardHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOwnerRewardHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
