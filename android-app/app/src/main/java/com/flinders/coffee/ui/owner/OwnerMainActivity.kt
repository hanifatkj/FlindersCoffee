package com.flinders.coffee.ui.owner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.flinders.coffee.R
import com.flinders.coffee.databinding.ActivityOwnerMainBinding

class OwnerMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOwnerMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOwnerMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            loadFragment(OwnerHomeFragment())
        }

        binding.ownerBottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_owner_home -> {
                    loadFragment(OwnerHomeFragment())
                    true
                }
                R.id.nav_owner_order -> {
                    loadFragment(OwnerBookingFragment())
                    true
                }
                R.id.nav_owner_floor -> {
                    loadFragment(OwnerFloorFragment())
                    true
                }
                R.id.nav_owner_manage -> {
                    loadFragment(OwnerMenuFragment())
                    true
                }
                R.id.nav_owner_profile -> {
                    loadFragment(OwnerProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    fun switchToTab(menuItemId: Int) {
        binding.ownerBottomNav.selectedItemId = menuItemId
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.ownerFragmentContainer, fragment)
            .commit()
    }
}
