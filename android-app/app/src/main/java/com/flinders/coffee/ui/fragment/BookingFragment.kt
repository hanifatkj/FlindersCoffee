package com.flinders.coffee.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.flinders.coffee.R
import com.flinders.coffee.data.TableRepository
import com.flinders.coffee.databinding.FragmentBookingBinding
import com.flinders.coffee.model.RoomInfo
import com.flinders.coffee.model.TableItem
import com.flinders.coffee.model.TableStatus
import com.flinders.coffee.ui.ActiveBookingActivity

class BookingFragment : Fragment() {

    private var _binding: FragmentBookingBinding? = null
    private val binding get() = _binding!!

    private var currentRoomCode = "A"
    private var selectedTable: TableItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Default selected table is A04 (Outdoor sudut depan kasir)
        selectedTable = TableRepository.getTable("A04")

        setupRoomTabs()
        updateQuotaDisplay()
        renderRoom(currentRoomCode)

        // Submit Booking -> Opens Layar 6 (Detail Booking Aktif)
        binding.btnSubmitBooking.setOnClickListener {
            val table = selectedTable
            if (table == null) {
                Toast.makeText(requireContext(), "Pilih meja yang tersedia terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (table.status != TableStatus.AVAILABLE && table.id != "A04") {
                Toast.makeText(requireContext(), "Meja ${table.id} sedang tidak tersedia untuk dibooking", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val room = TableRepository.getRoom(table.roomCode)
            val intent = Intent(requireContext(), ActiveBookingActivity::class.java).apply {
                putExtra("EXTRA_TABLE_ID", table.id)
                putExtra("EXTRA_ROOM_NAME", room?.name ?: "Ruang ${table.roomCode}")
                putExtra("EXTRA_ROOM_LOCATION", room?.location ?: "")
                putExtra("EXTRA_TABLE_DESC", table.description)
            }
            Toast.makeText(requireContext(), "Booking Meja ${table.id} Berhasil!", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
    }

    private fun setupRoomTabs() {
        binding.tabRuangA.setOnClickListener { switchRoom("A") }
        binding.tabRuangB.setOnClickListener { switchRoom("B") }
        binding.tabRuangC.setOnClickListener { switchRoom("C") }
    }

    private fun switchRoom(roomCode: String) {
        currentRoomCode = roomCode
        highlightTab(roomCode)
        renderRoom(roomCode)
    }

    private fun highlightTab(roomCode: String) {
        val activeBg = R.drawable.bg_primary_button
        val inactiveBg = R.drawable.bg_card_white
        val activeText = ContextCompat.getColor(requireContext(), R.color.white)
        val inactiveText = ContextCompat.getColor(requireContext(), R.color.flinders_text_muted)

        binding.tabRuangA.apply {
            setBackgroundResource(if (roomCode == "A") activeBg else inactiveBg)
            setTextColor(if (roomCode == "A") activeText else inactiveText)
        }
        binding.tabRuangB.apply {
            setBackgroundResource(if (roomCode == "B") activeBg else inactiveBg)
            setTextColor(if (roomCode == "B") activeText else inactiveText)
        }
        binding.tabRuangC.apply {
            setBackgroundResource(if (roomCode == "C") activeBg else inactiveBg)
            setTextColor(if (roomCode == "C") activeText else inactiveText)
        }
    }

    private fun renderRoom(roomCode: String) {
        val room = TableRepository.getRoom(roomCode) ?: return

        // Update header informasi ruangan
        binding.tvRoomTitle.text = room.name.uppercase()
        binding.tvRoomLocation.text = room.location
        binding.tvRoomSubtitle.text = "${room.tables.size} meja · ilustratif"
        binding.tvRoomDirection.text = when (roomCode) {
            "A" -> "Pintu masuk depan kasir ↓"
            "B" -> "Pintu masuk dari Ruang A ↓"
            else -> "Arah ke toilet & taman belakang ↓"
        }

        // Render meja di GridLayout
        val grid = binding.gridTables
        grid.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())

        for (table in room.tables) {
            val itemView = inflater.inflate(R.layout.item_table_card, grid, false)
            val card = itemView.findViewById<View>(R.id.cardTable)
            val tvNumber = itemView.findViewById<TextView>(R.id.tvTableNumber)
            val tvStatus = itemView.findViewById<TextView>(R.id.tvTableStatus)
            val tvCapacity = itemView.findViewById<TextView>(R.id.tvTableCapacity)

            tvNumber.text = table.number
            tvCapacity.text = "${table.capacity} kursi"

            val isSelected = selectedTable?.id == table.id

            when {
                isSelected -> {
                    card.setBackgroundResource(R.drawable.bg_table_selected)
                    tvNumber.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    tvStatus.text = "Dipilih"
                    tvCapacity.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
                table.status == TableStatus.OCCUPIED -> {
                    card.setBackgroundResource(R.drawable.bg_table_occupied)
                    tvNumber.setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_text_muted))
                    tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_text_muted))
                    tvStatus.text = "Terisi"
                    tvCapacity.setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_text_muted))
                }
                table.status == TableStatus.BOOKED -> {
                    card.setBackgroundResource(R.drawable.bg_table_booked)
                    tvNumber.setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_text_title))
                    tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_warning))
                    tvStatus.text = "Dibooking"
                    tvCapacity.setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_text_muted))
                }
                else -> {
                    card.setBackgroundResource(R.drawable.bg_table_available)
                    tvNumber.setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_text_title))
                    tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_text_muted))
                    tvStatus.text = "Tersedia"
                    tvCapacity.setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_text_muted))
                }
            }

            card.setOnClickListener {
                when (table.status) {
                    TableStatus.OCCUPIED -> {
                        Toast.makeText(requireContext(), "Meja ${table.id} sedang terisi oleh pelanggan lain.", Toast.LENGTH_SHORT).show()
                    }
                    TableStatus.BOOKED -> {
                        Toast.makeText(requireContext(), "Meja ${table.id} sudah dibooking orang lain.", Toast.LENGTH_SHORT).show()
                    }
                    TableStatus.AVAILABLE -> {
                        selectedTable = table
                        renderRoom(currentRoomCode)
                        updateSelectedCard(table, room)
                        Toast.makeText(requireContext(), "Meja ${table.id} dipilih · ${table.description}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(8, 8, 8, 8)
            }
            grid.addView(itemView, params)
        }

        selectedTable?.let { sel ->
            val selRoom = TableRepository.getRoom(sel.roomCode)
            if (selRoom != null) {
                updateSelectedCard(sel, selRoom)
            }
        }
    }

    private fun updateSelectedCard(table: TableItem, room: RoomInfo) {
        binding.tvSelectedTableCode.text = "${table.id} · ${room.name} (${room.location})"
        binding.tvSelectedTableDesc.text = table.description
        binding.btnSubmitBooking.text = "Booking 1 Meja (${table.id})"
    }

    private fun updateQuotaDisplay() {
        val total = TableRepository.getTotalTablesCount()
        val booked = TableRepository.getBookedOrOccupiedCount()
        val available = TableRepository.getAvailableCount()

        binding.tvQuotaKafe.text = "$booked/$total"
        binding.tvQuotaDescription.text =
            "Maks. 2 meja aktif per member. Total kafe: $total meja (A: 5, B: 5, C: 6). Sisa kuota kafe: $available meja."
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
