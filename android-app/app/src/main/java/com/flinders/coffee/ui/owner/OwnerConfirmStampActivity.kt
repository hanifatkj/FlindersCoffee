package com.flinders.coffee.ui.owner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.flinders.coffee.R
import com.flinders.coffee.data.MemberModel
import com.flinders.coffee.data.MemberRepository
import com.flinders.coffee.databinding.ActivityOwnerConfirmStampBinding

class OwnerConfirmStampActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOwnerConfirmStampBinding
    private var isTorchOn = false
    private var currentActiveMember: MemberModel? = null

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCameraScanner()
        } else {
            binding.layoutCameraPermissionPrompt.visibility = View.VISIBLE
            Toast.makeText(this, "Izin kamera diperlukan untuk scan QR member", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOwnerConfirmStampBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnRequestCameraPermission.setOnClickListener {
            checkCameraPermissionAndStart()
        }

        binding.btnToggleFlash.setOnClickListener {
            toggleFlashlight()
        }

        binding.btnSearchCustomer.setOnClickListener {
            val query = binding.etCustomerId.text.toString().trim()
            if (query.isNotEmpty()) {
                handleMemberLookup(query)
            } else {
                Toast.makeText(this, "Ketik ID atau nama member terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnConfirmRedeem.setOnClickListener {
            val member = currentActiveMember ?: return@setOnClickListener
            confirmRewardRedemption(member)
        }

        binding.btnAddSingleStamp.setOnClickListener {
            val member = currentActiveMember ?: return@setOnClickListener
            val (success, msg) = MemberRepository.addStamp(member.code)
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            if (success) {
                handleMemberLookup(member.code)
            }
        }

        binding.btnResetToScan.setOnClickListener {
            resetScannerState()
        }

        checkCameraPermissionAndStart()
    }

    private fun checkCameraPermissionAndStart() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCameraScanner()
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCameraScanner() {
        binding.layoutCameraPermissionPrompt.visibility = View.GONE
        binding.barcodeScannerView.resume()
        binding.barcodeScannerView.decodeSingle { result ->
            val scannedText = result.text
            runOnUiThread {
                Toast.makeText(this, "QR Terdeteksi: $scannedText", Toast.LENGTH_SHORT).show()
                binding.etCustomerId.setText(scannedText)
                handleMemberLookup(scannedText)
            }
        }
    }

    private fun handleMemberLookup(query: String) {
        val member = MemberRepository.findMember(query)
        if (member == null) {
            binding.cardCustomerResult.visibility = View.GONE
            Toast.makeText(this, "Member '$query' tidak ditemukan di database", Toast.LENGTH_SHORT).show()
            return
        }

        currentActiveMember = member
        binding.cardCustomerResult.visibility = View.VISIBLE

        binding.tvCustomerAvatar.text = member.name.take(1).uppercase()
        binding.tvCustomerName.text = member.name
        binding.tvCustomerPhone.text = "${member.code} · ${member.phone}"
        binding.tvCurrentStamps.text = "${member.stamps} / ${member.maxStamps}"

        // Reset sub-layouts
        binding.layoutRewardEligible.visibility = View.GONE
        binding.layoutStampIncomplete.visibility = View.GONE
        binding.layoutLocked24h.visibility = View.GONE
        binding.layoutRedemptionSuccess.visibility = View.GONE

        when {
            member.isLocked24h -> {
                binding.layoutLocked24h.visibility = View.VISIBLE
                binding.tvLockedNotice.text = "Member baru saja menukar hadiah pada ${member.lastRedemptionTime ?: "hari ini"}. Terkunci hingga: ${member.lockedUntil ?: "24 jam ke depan"}."
                binding.tvCurrentStamps.setTextColor(ContextCompat.getColor(this, R.color.flinders_error))
            }
            member.stamps >= member.maxStamps -> {
                binding.layoutRewardEligible.visibility = View.VISIBLE
                binding.tvCurrentStamps.setTextColor(ContextCompat.getColor(this, R.color.flinders_success))
            }
            else -> {
                binding.layoutStampIncomplete.visibility = View.VISIBLE
                val remaining = member.maxStamps - member.stamps
                binding.tvIncompleteNotice.text = "Member belum berhak tukar hadiah (perlu $remaining stamp lagi). Anda dapat menambahkan 1 stamp harian jika pesanan sudah dibayar."
                binding.tvCurrentStamps.setTextColor(ContextCompat.getColor(this, R.color.flinders_primary))
            }
        }
    }

    private fun confirmRewardRedemption(member: MemberModel) {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Tukar Hadiah?")
            .setMessage("Tukar 10 stamp milik ${member.name} (${member.code}) dengan 1 minuman gratis?\n\nStamp akan direset ke 0 dan akun terkunci dari penukaran hadiah selama 24 jam.")
            .setPositiveButton("Ya, Konfirmasi Tukar") { _, _ ->
                val (success, proofOrMsg) = MemberRepository.redeemReward(member.code)
                if (success) {
                    binding.layoutRewardEligible.visibility = View.GONE
                    binding.layoutRedemptionSuccess.visibility = View.VISIBLE
                    binding.tvRedemptionCodeProof.text = "Bukti Penukaran: $proofOrMsg"
                    binding.tvCurrentStamps.text = "0 / 10"
                    binding.tvCurrentStamps.setTextColor(ContextCompat.getColor(this, R.color.flinders_error))
                    Toast.makeText(this, "Hadiah berhasil ditukar! Berikan minuman gratis ke member.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, proofOrMsg, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun resetScannerState() {
        currentActiveMember = null
        binding.cardCustomerResult.visibility = View.GONE
        binding.etCustomerId.text?.clear()
        startCameraScanner()
    }

    private fun toggleFlashlight() {
        isTorchOn = !isTorchOn
        if (isTorchOn) {
            binding.barcodeScannerView.setTorchOn()
            binding.btnToggleFlash.setColorFilter(ContextCompat.getColor(this, R.color.flinders_success))
            Toast.makeText(this, "Lampu kamera menyala", Toast.LENGTH_SHORT).show()
        } else {
            binding.barcodeScannerView.setTorchOff()
            binding.btnToggleFlash.setColorFilter(ContextCompat.getColor(this, R.color.flinders_text_white))
            Toast.makeText(this, "Lampu kamera mati", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            binding.barcodeScannerView.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.barcodeScannerView.pause()
    }
}
