package com.flinders.coffee.ui.owner

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.flinders.coffee.R
import com.flinders.coffee.databinding.FragmentOwnerBookingBinding
import com.flinders.coffee.model.BookingFilterStatus
import com.flinders.coffee.model.BookingOrder

class OwnerBookingFragment : Fragment() {

    private var _binding: FragmentOwnerBookingBinding? = null
    private val binding get() = _binding!!

    private var currentFilter: BookingFilterStatus = BookingFilterStatus.ALL

    // Data sample reservasi kafe Flinders
    private val bookingList = mutableListOf(
        BookingOrder(
            id = "BK-001",
            customerName = "Andi Pratama",
            tableId = "A03",
            roomName = "Ruang A (Outdoor depan kasir)",
            timeSlot = "14:00 - 16:00",
            guestCount = 2,
            status = BookingFilterStatus.ACTIVE
        ),
        BookingOrder(
            id = "BK-002",
            customerName = "Sari Dewi",
            tableId = "B03",
            roomName = "Ruang B (Indoor bagian tengah)",
            timeSlot = "16:30 - 18:30",
            guestCount = 2,
            status = BookingFilterStatus.ACTIVE
        ),
        BookingOrder(
            id = "BK-003",
            customerName = "Rian Hidayat",
            tableId = "C05",
            roomName = "Ruang C (Didekat WC/belakang)",
            timeSlot = "19:00 - 21:00",
            guestCount = 4,
            status = BookingFilterStatus.ACTIVE
        ),
        BookingOrder(
            id = "BK-004",
            customerName = "Budi Santoso",
            tableId = "A01",
            roomName = "Ruang A (Outdoor depan kasir)",
            timeSlot = "10:00 - 12:00",
            guestCount = 3,
            status = BookingFilterStatus.COMPLETED
        ),
        BookingOrder(
            id = "BK-005",
            customerName = "Citra Lestari",
            tableId = "B01",
            roomName = "Ruang B (Indoor bagian tengah)",
            timeSlot = "11:30 - 13:30",
            guestCount = 4,
            status = BookingFilterStatus.COMPLETED
        ),
        BookingOrder(
            id = "BK-006",
            customerName = "Dimas Anggara",
            tableId = "C02",
            roomName = "Ruang C (Didekat WC/belakang)",
            timeSlot = "13:00 - 15:00",
            guestCount = 2,
            status = BookingFilterStatus.CANCELLED
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOwnerBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTabClickListeners()
        applyFilter(BookingFilterStatus.ALL)
    }

    private fun setupTabClickListeners() {
        binding.tabAll.setOnClickListener {
            applyFilter(BookingFilterStatus.ALL)
        }
        binding.tabActive.setOnClickListener {
            applyFilter(BookingFilterStatus.ACTIVE)
        }
        binding.tabCompleted.setOnClickListener {
            applyFilter(BookingFilterStatus.COMPLETED)
        }
        binding.tabCancelled.setOnClickListener {
            applyFilter(BookingFilterStatus.CANCELLED)
        }
    }

    private fun applyFilter(filter: BookingFilterStatus) {
        currentFilter = filter
        updateTabStyles(filter)
        renderBookingCards(filter)
    }

    private fun updateTabStyles(activeFilter: BookingFilterStatus) {
        val activeBg = R.drawable.bg_tab_active
        val inactiveBg = R.drawable.bg_tab_inactive
        val activeColor = ContextCompat.getColor(requireContext(), R.color.flinders_text_white)
        val inactiveColor = ContextCompat.getColor(requireContext(), R.color.flinders_text_muted)

        // Reset all tabs
        binding.tabAll.setBackgroundResource(inactiveBg)
        binding.tabAll.setTextColor(inactiveColor)
        binding.tabActive.setBackgroundResource(inactiveBg)
        binding.tabActive.setTextColor(inactiveColor)
        binding.tabCompleted.setBackgroundResource(inactiveBg)
        binding.tabCompleted.setTextColor(inactiveColor)
        binding.tabCancelled.setBackgroundResource(inactiveBg)
        binding.tabCancelled.setTextColor(inactiveColor)

        // Highlight selected tab
        when (activeFilter) {
            BookingFilterStatus.ALL -> {
                binding.tabAll.setBackgroundResource(activeBg)
                binding.tabAll.setTextColor(activeColor)
                binding.tvBookingSubtitle.text = "Menampilkan semua riwayat reservasi (${bookingList.size} meja)"
            }
            BookingFilterStatus.ACTIVE -> {
                binding.tabActive.setBackgroundResource(activeBg)
                binding.tabActive.setTextColor(activeColor)
                val count = bookingList.count { it.status == BookingFilterStatus.ACTIVE }
                binding.tvBookingSubtitle.text = "Menampilkan reservasi yang sedang aktif ($count meja menunggu check-in)"
            }
            BookingFilterStatus.COMPLETED -> {
                binding.tabCompleted.setBackgroundResource(activeBg)
                binding.tabCompleted.setTextColor(activeColor)
                val count = bookingList.count { it.status == BookingFilterStatus.COMPLETED }
                binding.tvBookingSubtitle.text = "Menampilkan reservasi yang telah selesai ($count meja)"
            }
            BookingFilterStatus.CANCELLED -> {
                binding.tabCancelled.setBackgroundResource(activeBg)
                binding.tabCancelled.setTextColor(activeColor)
                val count = bookingList.count { it.status == BookingFilterStatus.CANCELLED }
                binding.tvBookingSubtitle.text = "Menampilkan reservasi yang dibatalkan ($count meja)"
            }
        }
    }

    private fun renderBookingCards(filter: BookingFilterStatus) {
        val container = binding.containerBookingCards
        container.removeAllViews()

        val filteredList = when (filter) {
            BookingFilterStatus.ALL -> bookingList
            else -> bookingList.filter { it.status == filter }
        }

        if (filteredList.isEmpty()) {
            binding.layoutEmptyState.visibility = View.VISIBLE
            val label = when (filter) {
                BookingFilterStatus.ACTIVE -> "aktif"
                BookingFilterStatus.COMPLETED -> "selesai"
                BookingFilterStatus.CANCELLED -> "dibatalkan"
                else -> ""
            }
            binding.tvEmptyTitle.text = "Tidak ada reservasi $label saat ini"
        } else {
            binding.layoutEmptyState.visibility = View.GONE
            val inflater = LayoutInflater.from(requireContext())

            for (booking in filteredList) {
                val cardView = inflater.inflate(R.layout.item_owner_booking_card, container, false)
                val tvName = cardView.findViewById<TextView>(R.id.tvOrderCustomerName)
                val tvBadge = cardView.findViewById<TextView>(R.id.tvOrderStatusBadge)
                val tvTable = cardView.findViewById<TextView>(R.id.tvOrderTable)
                val tvTime = cardView.findViewById<TextView>(R.id.tvOrderTime)
                val tvGuests = cardView.findViewById<TextView>(R.id.tvOrderGuests)
                val layoutActions = cardView.findViewById<View>(R.id.layoutActionButtons)
                val btnCheckIn = cardView.findViewById<Button>(R.id.btnOrderCheckIn)
                val btnCancel = cardView.findViewById<Button>(R.id.btnOrderCancel)

                tvName.text = booking.customerName
                tvTable.text = "Meja ${booking.tableId} (${booking.roomName})"
                tvTime.text = booking.timeSlot
                tvGuests.text = "${booking.guestCount} orang"

                when (booking.status) {
                    BookingFilterStatus.ACTIVE -> {
                        tvBadge.text = "Aktif"
                        tvBadge.setBackgroundResource(R.drawable.bg_badge_green)
                        tvBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_text_white))
                        layoutActions.visibility = View.VISIBLE

                        btnCheckIn.setOnClickListener {
                            val intent = Intent(requireContext(), OwnerCheckInActivity::class.java).apply {
                                putExtra("EXTRA_CUSTOMER_NAME", booking.customerName)
                                putExtra("EXTRA_TABLE_ID", booking.tableId)
                                putExtra("EXTRA_TIME", booking.timeSlot)
                            }
                            startActivity(intent)
                        }

                        btnCancel.setOnClickListener {
                            AlertDialog.Builder(requireContext())
                                .setTitle("Batalkan Reservasi?")
                                .setMessage("Batalkan reservasi meja ${booking.tableId} untuk pelanggan ${booking.customerName}?")
                                .setPositiveButton("Ya, Batalkan") { _, _ ->
                                    booking.status = BookingFilterStatus.CANCELLED
                                    applyFilter(currentFilter)
                                    Toast.makeText(requireContext(), "Reservasi ${booking.customerName} berhasil dibatalkan", Toast.LENGTH_SHORT).show()
                                }
                                .setNegativeButton("Kembali", null)
                                .show()
                        }
                    }
                    BookingFilterStatus.COMPLETED -> {
                        tvBadge.text = "Selesai"
                        tvBadge.setBackgroundResource(R.drawable.bg_badge_grey)
                        tvBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_text_muted))
                        layoutActions.visibility = View.GONE
                    }
                    BookingFilterStatus.CANCELLED -> {
                        tvBadge.text = "Dibatalkan"
                        tvBadge.setBackgroundResource(R.drawable.bg_badge_red)
                        tvBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_error))
                        layoutActions.visibility = View.GONE
                    }
                    BookingFilterStatus.ALL -> {}
                }

                container.addView(cardView)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
