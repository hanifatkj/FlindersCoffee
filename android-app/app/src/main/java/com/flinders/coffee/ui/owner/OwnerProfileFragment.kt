package com.flinders.coffee.ui.owner

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.flinders.coffee.databinding.FragmentOwnerProfileBinding
import com.flinders.coffee.ui.LoginActivity

class OwnerProfileFragment : Fragment() {

    private var _binding: FragmentOwnerProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOwnerProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.menuConfirmStamp.setOnClickListener {
            startActivity(Intent(requireContext(), OwnerConfirmStampActivity::class.java))
        }

        binding.menuRewardHistory.setOnClickListener {
            startActivity(Intent(requireContext(), OwnerRewardHistoryActivity::class.java))
        }

        binding.menuAddItem.setOnClickListener {
            startActivity(Intent(requireContext(), OwnerAddMenuActivity::class.java))
        }

        binding.menuReport.setOnClickListener {
            startActivity(Intent(requireContext(), OwnerReportActivity::class.java))
        }

        binding.menuEditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), OwnerProfileActivity::class.java))
        }

        binding.menuAboutApp.setOnClickListener {
            startActivity(Intent(requireContext(), com.flinders.coffee.ui.AboutActivity::class.java))
        }

        binding.menuOwnerLogout.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Keluar Mode Owner")
                .setMessage("Apakah Anda yakin ingin keluar dari akun Owner?")
                .setPositiveButton("Keluar") { _, _ ->
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    activity?.finish()
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
