package com.flinders.coffee.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.flinders.coffee.R
import com.flinders.coffee.databinding.ActivityMainBinding
import com.flinders.coffee.ui.fragment.AccountFragment
import com.flinders.coffee.ui.fragment.BookingFragment
import com.flinders.coffee.ui.fragment.HomeFragment
import com.flinders.coffee.ui.fragment.NotificationFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_beranda -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_booking -> {
                    loadFragment(BookingFragment())
                    true
                }
                R.id.nav_notifikasi -> {
                    loadFragment(NotificationFragment())
                    true
                }
                R.id.nav_akun -> {
                    loadFragment(AccountFragment())
                    true
                }
                else -> false
            }
        }
    }

    fun navigateToBooking() {
        binding.bottomNavigation.selectedItemId = R.id.nav_booking
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
