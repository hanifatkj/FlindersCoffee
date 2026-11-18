package com.flinders.coffee.ui.owner

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.flinders.coffee.R
import com.flinders.coffee.data.MenuRepository
import com.flinders.coffee.databinding.ActivityOwnerAddMenuBinding
import com.flinders.coffee.model.MenuItem
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class OwnerAddMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOwnerAddMenuBinding

    private val rupiahFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID")).apply {
        maximumFractionDigits = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOwnerAddMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupCategorySpinner()
        setupLiveProfitCalculation()
        setupStatusSwitch()
        setupEffectiveDate()
        setupActions()
    }

    private fun setupToolbar() {
        binding.btnBackHeader.setOnClickListener {
            finish()
        }
    }

    private fun setupCategorySpinner() {
        val categories = arrayOf("Kopi", "Nonkopi", "Makanan", "Pastry & Camilan")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        binding.spinnerCategory.adapter = adapter
    }

    private fun setupLiveProfitCalculation() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                calculateMarginPreview()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etMenuPrice.addTextChangedListener(watcher)
        binding.etMenuHpp.addTextChangedListener(watcher)
    }

    private fun calculateMarginPreview() {
        val priceStr = binding.etMenuPrice.text.toString().replace("[^0-9]".toRegex(), "")
        val hppStr = binding.etMenuHpp.text.toString().replace("[^0-9]".toRegex(), "")

        val price = priceStr.toLongOrNull() ?: 0L
        val hpp = hppStr.toLongOrNull() ?: 0L

        if (price > 0 && hpp > 0) {
            val profit = price - hpp
            val margin = (profit.toDouble() / price.toDouble()) * 100.0

            if (profit >= 0) {
                binding.tvMarginPreviewText.text =
                    "Estimasi Laba Bersih: ${rupiahFormat.format(profit)} / porsi (Margin: ${String.format(Locale.US, "%.1f", margin)}%)"
                binding.tvMarginPreviewText.setTextColor(ContextCompat.getColor(this, R.color.flinders_primary))
            } else {
                binding.tvMarginPreviewText.text =
                    "⚠️ Peringatan: HPP melebihi harga jual! (Rugi ${rupiahFormat.format(Math.abs(profit))} / porsi)"
                binding.tvMarginPreviewText.setTextColor(ContextCompat.getColor(this, R.color.flinders_error))
            }
        } else {
            binding.tvMarginPreviewText.text = "Estimasi Laba: Masukkan harga jual & HPP untuk kalkulasi"
            binding.tvMarginPreviewText.setTextColor(ContextCompat.getColor(this, R.color.flinders_text_muted))
        }
    }

    private fun setupStatusSwitch() {
        binding.switchMenuStatus.setOnCheckedChangeListener { _, isChecked ->
            binding.tvStatusTitle.text = if (isChecked) "Status menu · Aktif" else "Status menu · Nonaktif"
        }
    }

    private fun setupEffectiveDate() {
        val sdf = SimpleDateFormat("dd MMM yyyy · HH.mm 'WITA'", Locale.forLanguageTag("id-ID"))
        sdf.timeZone = TimeZone.getTimeZone("Asia/Makassar")
        binding.tvEffectiveDate.text = sdf.format(Date())
    }

    private fun setupActions() {
        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.boxUploadPhoto.setOnClickListener {
            Toast.makeText(this, "Pilih foto menu dari galeri (opsional)", Toast.LENGTH_SHORT).show()
        }

        binding.btnSaveMenu.setOnClickListener {
            saveMenuItem()
        }
    }

    private fun saveMenuItem() {
        val name = binding.etMenuName.text.toString().trim()
        val category = binding.spinnerCategory.selectedItem?.toString() ?: "Kopi"
        val priceStr = binding.etMenuPrice.text.toString().replace("[^0-9]".toRegex(), "")
        val hppStr = binding.etMenuHpp.text.toString().replace("[^0-9]".toRegex(), "")
        val isActive = binding.switchMenuStatus.isChecked

        if (name.isEmpty()) {
            Toast.makeText(this, "Harap masukkan nama menu", Toast.LENGTH_SHORT).show()
            binding.etMenuName.requestFocus()
            return
        }

        val price = priceStr.toLongOrNull() ?: 0L
        if (price <= 0) {
            Toast.makeText(this, "Harga jual harus lebih besar dari 0", Toast.LENGTH_SHORT).show()
            binding.etMenuPrice.requestFocus()
            return
        }

        val hpp = hppStr.toLongOrNull() ?: 0L
        if (hpp <= 0) {
            Toast.makeText(this, "HPP / biaya produksi bahan harus lebih besar dari 0", Toast.LENGTH_SHORT).show()
            binding.etMenuHpp.requestFocus()
            return
        }

        val profit = price - hpp
        val margin = (profit.toDouble() / price.toDouble()) * 100.0

        val newMenu = MenuItem(
            id = "MN-" + System.currentTimeMillis().toString().takeLast(4),
            name = name,
            category = category,
            price = price,
            hpp = hpp,
            isActive = isActive,
            effectiveDate = binding.tvEffectiveDate.text.toString()
        )

        // Simpan ke Repository Menu & MySQL XAMPP
        MenuRepository.addMenu(newMenu)
        com.flinders.coffee.data.ApiClient.saveMenu(
            newMenu.id,
            newMenu.name,
            newMenu.category,
            newMenu.price,
            newMenu.hpp,
            newMenu.description
        ) { _, _ -> }

        AlertDialog.Builder(this)
            .setTitle("Menu Berhasil Disimpan!")
            .setMessage(
                "Rincian Entri Baru:\n\n" +
                "• Menu: $name ($category)\n" +
                "• Harga Jual Kasir: ${rupiahFormat.format(price)}\n" +
                "• HPP Produksi: ${rupiahFormat.format(hpp)}\n" +
                "• Laba Bersih Per Porsi: ${rupiahFormat.format(profit)} (${String.format(Locale.US, "%.1f", margin)}%)\n\n" +
                "Data HPP telah dipisahkan di database untuk kalkulasi laba bersih otomatis pada transaksi kasir."
            )
            .setPositiveButton("Selesai") { _, _ ->
                Toast.makeText(this, "Katalog menu diperbarui!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setCancelable(false)
            .show()
    }
}
