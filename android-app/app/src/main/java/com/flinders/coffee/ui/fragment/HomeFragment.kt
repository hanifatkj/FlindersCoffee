package com.flinders.coffee.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.flinders.coffee.databinding.FragmentHomeBinding
import com.flinders.coffee.ui.MainActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Toggle demo state: false = 7/10 Stamp (Layar 3), true = 10/10 Reward (Layar 4)
    private var isRewardState = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUiState()

        // Toggle button to demo both screens to the lecturer
        binding.btnToggleDemoState.setOnClickListener {
            isRewardState = !isRewardState
            updateUiState()
        }

        binding.btnHomeBookingMeja.setOnClickListener {
            (activity as? MainActivity)?.navigateToBooking()
        }
    }

    private fun updateUiState() {
        if (!isRewardState) {
            // Layar 3: Kondisi 7/10 Stamp
            binding.tvDemoToggleLabel.text = "Mode: Layar 3 (7/10 Stamp) · Klik untuk uji Layar 4"
            binding.tvHomeTitle.text = "Halo, Rina."
            binding.tvHomeSubtitle.text = "Momen hangat, hadiah lebih dekat."
            binding.layoutStateA.visibility = View.VISIBLE
            binding.layoutStateB.visibility = View.GONE
        } else {
            // Layar 4: Kondisi 10/10 Stamp (Reward Siap)
            binding.tvDemoToggleLabel.text = "Mode: Layar 4 (10/10 Hadiah) · Klik untuk kembali ke Layar 3"
            binding.tvHomeTitle.text = "Kopi berikutnya,\nhadiah."
            binding.tvHomeSubtitle.text = "Contoh kondisi: 10/10 · alternatif beranda"
            binding.layoutStateA.visibility = View.GONE
            binding.layoutStateB.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
