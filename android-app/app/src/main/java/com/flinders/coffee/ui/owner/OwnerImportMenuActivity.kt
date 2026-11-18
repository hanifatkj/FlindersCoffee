package com.flinders.coffee.ui.owner

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.flinders.coffee.data.MenuRepository
import com.flinders.coffee.databinding.ActivityOwnerImportMenuBinding
import com.flinders.coffee.model.MenuItem
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class OwnerImportMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOwnerImportMenuBinding

    private val rupiahFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID")).apply {
        maximumFractionDigits = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOwnerImportMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHeader()
        setupSpinners()
        setupFileActions()
        setupImportAction()
    }

    private fun setupHeader() {
        binding.btnBackHeader.setOnClickListener { finish() }
        binding.btnCancel.setOnClickListener { finish() }

        val sdf = SimpleDateFormat("dd MMM yyyy · HH.mm 'WITA'", Locale.forLanguageTag("id-ID"))
        sdf.timeZone = TimeZone.getTimeZone("Asia/Makassar")
        binding.tvDefaultEffectiveDate.text = sdf.format(Date())
    }

    private fun setupSpinners() {
        fun setDropdown(spinner: android.widget.Spinner, target: String) {
            val options = arrayOf(target, "Abaikan kolom ini")
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, options)
            spinner.adapter = adapter
        }

        setDropdown(binding.spinnerMapName, "Nama menu")
        setDropdown(binding.spinnerMapCategory, "Kategori")
        setDropdown(binding.spinnerMapPrice, "Harga jual")
        setDropdown(binding.spinnerMapHpp, "HPP / porsi")
        setDropdown(binding.spinnerMapActive, "Status aktif")
        setDropdown(binding.spinnerMapDate, "Tanggal efektif")

        val matchRules = arrayOf("ID menu, lalu nama persis", "Nama persis saja", "Selalu buat baru")
        val matchAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, matchRules)
        binding.spinnerMatchRule.adapter = matchAdapter
    }

    private fun setupFileActions() {
        binding.btnDownloadTemplate.setOnClickListener {
            Toast.makeText(this, "Template spreadsheet (template_menu_flinders.xlsx) diunduh!", Toast.LENGTH_SHORT).show()
        }

        binding.btnChangeFile.setOnClickListener {
            Toast.makeText(this, "Pilih spreadsheet katalog menu (.xlsx / .csv)", Toast.LENGTH_SHORT).show()
        }

        binding.boxSelectedFile.setOnClickListener {
            Toast.makeText(this, "File contoh: menu_flinders_04nov2026.xlsx (3 baris)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupImportAction() {
        binding.btnCheckData.setOnClickListener {
            // Simulasi validasi 3 baris dari file terpilih
            val sampleItems = listOf(
                MenuItem("MN-03", "Caramel Macchiato", "Kopi", 28000L, 11000L, true, effectiveDate = binding.tvDefaultEffectiveDate.text.toString()),
                MenuItem("MN-05", "Hazelnut Latte", "Kopi", 28000L, 11000L, true, effectiveDate = binding.tvDefaultEffectiveDate.text.toString()),
                MenuItem("MN-11", "Cinnamon Roll", "Makanan", 24000L, 10000L, true, effectiveDate = binding.tvDefaultEffectiveDate.text.toString())
            )

            val summaryMessage = StringBuilder()
            summaryMessage.append("Validasi 3 Baris Spreadsheet Berhasil!\n\n")
            for ((index, item) in sampleItems.withIndex()) {
                summaryMessage.append("${index + 1}. ${item.name} (${item.category})\n")
                summaryMessage.append("   • Jual: ${rupiahFormat.format(item.price)} | HPP: ${rupiahFormat.format(item.hpp)}\n")
                summaryMessage.append("   • Laba/Porsi: ${rupiahFormat.format(item.profitPerPortion)} (${String.format(Locale.US, "%.1f", item.marginPercentage)}%)\n\n")
            }
            summaryMessage.append("Apakah Anda ingin mengonfirmasi dan mengimpor menu ini ke database?")

            AlertDialog.Builder(this)
                .setTitle("Langkah 2: Validasi Data Impor")
                .setMessage(summaryMessage.toString())
                .setPositiveButton("Konfirmasi & Impor") { _, _ ->
                    val importedCount = MenuRepository.importMenus(sampleItems)
                    for (item in sampleItems) {
                        com.flinders.coffee.data.ApiClient.saveMenu(item.id, item.name, item.category, item.price, item.hpp, item.description) { _, _ -> }
                    }
                    AlertDialog.Builder(this)
                        .setTitle("Langkah 3: Impor Sukses!")
                        .setMessage(
                            "$importedCount menu berhasil diimpor ke database Flinders Coffee!\n\n" +
                            "Katalog menu di aplikasi kasir sekarang otomatis menggunakan data harga jual dan HPP produksi terbaru ini."
                        )
                        .setPositiveButton("Selesai") { _, _ ->
                            finish()
                        }
                        .setCancelable(false)
                        .show()
                }
                .setNegativeButton("Periksa Ulang", null)
                .show()
        }
    }
}
