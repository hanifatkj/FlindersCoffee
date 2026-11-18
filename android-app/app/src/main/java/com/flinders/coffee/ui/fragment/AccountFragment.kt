package com.flinders.coffee.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.flinders.coffee.databinding.FragmentAccountBinding
import com.flinders.coffee.ui.LoginActivity

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnEditProfile.setOnClickListener {
            Toast.makeText(requireContext(), "Menu edit profil Rina", Toast.LENGTH_SHORT).show()
        }

        binding.btnTermsInfo.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Syarat & Ketentuan Flinders Coffee")
                .setMessage("1. Setiap 1 stamp diberikan per hari kalender WITA setelah pesanan berstatus Dibayar di kasir.\n\n2. 10 stamp dapat ditukar dengan 1 minuman gratis.\n\n3. Penukaran hadiah hanya dapat dilakukan langsung bersama Owner di kafe.\n\n4. Booking meja berlaku maksimal 2 jam dan dapat dibatalkan otomatis jika belum check-in.")
                .setPositiveButton("Tutup", null)
                .show()
        }

        binding.btnAboutApp.setOnClickListener {
            startActivity(Intent(requireContext(), com.flinders.coffee.ui.AboutActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Keluar Akun")
                .setMessage("Apakah Anda yakin ingin keluar dari akun Rina?")
                .setPositiveButton("Keluar") { _, _ ->
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
