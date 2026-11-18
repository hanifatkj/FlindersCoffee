package com.flinders.coffee.ui.owner

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.flinders.coffee.R
import com.flinders.coffee.data.MenuRepository
import com.flinders.coffee.databinding.FragmentOwnerMenuBinding
import com.flinders.coffee.model.MenuItem
import com.google.android.material.button.MaterialButton
import java.text.NumberFormat
import java.util.Locale

class OwnerMenuFragment : Fragment() {

    private var _binding: FragmentOwnerMenuBinding? = null
    private val binding get() = _binding!!

    private var selectedCategory: String = "Semua"
    private var selectedStatus: Boolean? = true
    private var searchQuery: String = ""

    private val rupiahFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID")).apply {
        maximumFractionDigits = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOwnerMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTopActions()
        setupSearch()
        setupCategoryChips()
        setupStatusChips()
        renderMenus()
    }

    override fun onResume() {
        super.onResume()
        renderMenus()
    }

    private fun setupTopActions() {
        binding.btnNewMenu.setOnClickListener {
            startActivity(Intent(requireContext(), OwnerAddMenuActivity::class.java))
        }

        binding.btnImportSpreadsheet.setOnClickListener {
            startActivity(Intent(requireContext(), OwnerImportMenuActivity::class.java))
        }

        binding.btnMenuSettings.setOnClickListener {
            Toast.makeText(requireContext(), "Pengaturan Katalog Menu & HPP Flinders", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSearch() {
        binding.etSearchMenu.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                searchQuery = s?.toString()?.trim() ?: ""
                renderMenus()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupCategoryChips() {
        val chips = listOf(
            binding.chipCategoryAll to "Semua",
            binding.chipCategoryKopi to "Coffee",
            binding.chipCategoryFlavoured to "Flavoured Coffee",
            binding.chipCategorySignature to "Signature",
            binding.chipCategoryAmericano to "Americano Series",
            binding.chipCategoryNonKopi to "Non Coffee",
            binding.chipCategoryFrappe to "Frappe",
            binding.chipCategoryTea to "Tea",
            binding.chipCategoryMakanan to "Meal & Snack"
        )

        for ((chipView, category) in chips) {
            chipView.setOnClickListener {
                selectedCategory = category
                updateCategoryChipsUi(chips)
                renderMenus()
            }
        }
    }

    private fun updateCategoryChipsUi(chips: List<Pair<TextView, String>>) {
        for ((chipView, category) in chips) {
            if (category == selectedCategory) {
                chipView.setBackgroundResource(R.drawable.bg_pill_primary)
                chipView.setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_text_white))
                chipView.setTypeface(null, android.graphics.Typeface.BOLD)
            } else {
                chipView.setBackgroundResource(R.drawable.bg_pill_unselected)
                chipView.setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_text_title))
                chipView.setTypeface(null, android.graphics.Typeface.NORMAL)
            }
        }
    }

    private fun setupStatusChips() {
        binding.chipStatusActive.setOnClickListener {
            selectedStatus = true
            binding.chipStatusActive.setBackgroundResource(R.drawable.bg_pill_primary)
            binding.chipStatusActive.setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_text_white))
            binding.chipStatusActive.setTypeface(null, android.graphics.Typeface.BOLD)

            binding.chipStatusInactive.setBackgroundResource(R.drawable.bg_pill_unselected)
            binding.chipStatusInactive.setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_text_title))
            binding.chipStatusInactive.setTypeface(null, android.graphics.Typeface.NORMAL)

            renderMenus()
        }

        binding.chipStatusInactive.setOnClickListener {
            selectedStatus = false
            binding.chipStatusInactive.setBackgroundResource(R.drawable.bg_pill_primary)
            binding.chipStatusInactive.setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_text_white))
            binding.chipStatusInactive.setTypeface(null, android.graphics.Typeface.BOLD)

            binding.chipStatusActive.setBackgroundResource(R.drawable.bg_pill_unselected)
            binding.chipStatusActive.setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_text_title))
            binding.chipStatusActive.setTypeface(null, android.graphics.Typeface.NORMAL)

            renderMenus()
        }
    }

    private fun renderMenus() {
        binding.containerMenuList.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())
        val menus = MenuRepository.filterMenus(searchQuery, selectedCategory, selectedStatus)

        if (menus.isEmpty()) {
            val emptyTv = TextView(requireContext()).apply {
                text = "Tidak ada menu yang cocok dengan filter."
                setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_text_muted))
                textSize = 14f
                setPadding(0, 40, 0, 40)
                gravity = android.view.Gravity.CENTER
            }
            binding.containerMenuList.addView(emptyTv)
            return
        }

        for (item in menus) {
            val cardView = inflater.inflate(R.layout.item_owner_menu_card, binding.containerMenuList, false)

            val tvName = cardView.findViewById<TextView>(R.id.tvCardMenuName)
            val tvCategory = cardView.findViewById<TextView>(R.id.tvCardMenuCategory)
            val tvStatus = cardView.findViewById<TextView>(R.id.tvCardMenuStatus)
            val tvPrice = cardView.findViewById<TextView>(R.id.tvCardMenuPrice)
            val tvHpp = cardView.findViewById<TextView>(R.id.tvCardMenuHpp)
            val btnEdit = cardView.findViewById<MaterialButton>(R.id.btnCardEditMenu)
            val btnToggle = cardView.findViewById<MaterialButton>(R.id.btnCardToggleStatus)

            tvName.text = item.name
            tvCategory.text = item.category
            tvPrice.text = rupiahFormat.format(item.price)
            tvHpp.text = rupiahFormat.format(item.hpp)

            if (item.isActive) {
                tvStatus.text = "● Aktif"
                tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_primary))
                tvStatus.setBackgroundResource(R.drawable.bg_info_card)
                btnToggle.text = "Nonaktifkan"
            } else {
                tvStatus.text = "● Nonaktif"
                tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_text_muted))
                tvStatus.setBackgroundResource(R.drawable.bg_pill_unselected)
                btnToggle.text = "Aktifkan"
            }

            btnToggle.setOnClickListener {
                val newStatus = MenuRepository.toggleStatus(item.id)
                val statusText = if (newStatus) "diaktifkan kembali" else "dinonaktifkan"
                Toast.makeText(requireContext(), "'${item.name}' $statusText.", Toast.LENGTH_SHORT).show()
                renderMenus()

                // Sinkronkan perubahan status menu ke MySQL
                com.flinders.coffee.data.ApiClient.toggleMenu(item.id) { _, _ -> }
            }

            btnEdit.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Edit Menu: ${item.name}")
                    .setMessage(
                        "Kategori: ${item.category}\n" +
                        "Harga Jual Saat Ini: ${rupiahFormat.format(item.price)}\n" +
                        "HPP / Porsi: ${rupiahFormat.format(item.hpp)}\n" +
                        "Estimasi Margin: ${String.format(Locale.US, "%.1f", item.marginPercentage)}%\n\n" +
                        "Untuk memperbarui harga jual atau HPP, gunakan penambahan snapshot atau impor spreadsheet baru."
                    )
                    .setPositiveButton("Tutup", null)
                    .show()
            }

            binding.containerMenuList.addView(cardView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
