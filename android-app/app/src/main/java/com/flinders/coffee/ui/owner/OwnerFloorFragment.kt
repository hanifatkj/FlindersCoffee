package com.flinders.coffee.ui.owner

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
import com.flinders.coffee.data.ApiClient
import com.flinders.coffee.data.TableRepository
import com.flinders.coffee.databinding.FragmentOwnerFloorBinding
import com.flinders.coffee.model.RoomInfo
import com.flinders.coffee.model.TableItem
import com.flinders.coffee.model.TableStatus

class OwnerFloorFragment : Fragment() {

    private var _binding: FragmentOwnerFloorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOwnerFloorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadRealTables()
    }

    override fun onResume() {
        super.onResume()
        loadRealTables()
    }

    private fun loadRealTables() {
        renderAllRooms()
        updateSummary()
        TableRepository.syncFromApi {
            if (_binding != null) {
                renderAllRooms()
                updateSummary()
            }
        }
    }

    private fun renderAllRooms() {
        val roomA = TableRepository.getRoom("A")
        val roomB = TableRepository.getRoom("B")
        val roomC = TableRepository.getRoom("C")

        if (roomA != null) populateGrid(binding.gridOwnerRoomA, roomA)
        if (roomB != null) populateGrid(binding.gridOwnerRoomB, roomB)
        if (roomC != null) populateGrid(binding.gridOwnerRoomC, roomC)
    }

    private fun populateGrid(grid: GridLayout, room: RoomInfo) {
        grid.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())

        for (table in room.tables) {
            val itemView = inflater.inflate(R.layout.item_table_card, grid, false)
            val card = itemView.findViewById<View>(R.id.cardTable)
            val tvNumber = itemView.findViewById<TextView>(R.id.tvTableNumber)
            val tvStatus = itemView.findViewById<TextView>(R.id.tvTableStatus)
            val tvCapacity = itemView.findViewById<TextView>(R.id.tvTableCapacity)

            tvNumber.text = "${table.id}"
            tvCapacity.text = "${table.capacity} kursi"

            when (table.status) {
                TableStatus.OCCUPIED -> {
                    card.setBackgroundResource(R.drawable.bg_table_occupied)
                    tvNumber.setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_text_muted))
                    tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_text_muted))
                    tvStatus.text = "Terisi"
                    tvCapacity.setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_text_muted))
                }
                TableStatus.BOOKED -> {
                    card.setBackgroundResource(R.drawable.bg_table_booked)
                    tvNumber.setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_text_title))
                    tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_warning))
                    tvStatus.text = "Dipesan"
                    tvCapacity.setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_text_muted))
                }
                TableStatus.AVAILABLE -> {
                    card.setBackgroundResource(R.drawable.bg_table_available)
                    tvNumber.setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_text_title))
                    tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_success))
                    tvStatus.text = "Tersedia"
                    tvCapacity.setTextColor(ContextCompat.getColor(requireContext(), R.color.flinders_text_muted))
                }
            }

            card.setOnClickListener {
                val options = arrayOf("Tersedia (Kosong)", "Dipesan (Booking)", "Terisi (Tamu Sedang Duduk)")
                val currentIdx = when (table.status) {
                    TableStatus.AVAILABLE -> 0
                    TableStatus.BOOKED -> 1
                    TableStatus.OCCUPIED -> 2
                }
                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Ubah Status Meja ${table.id} (${room.name})")
                    .setSingleChoiceItems(options, currentIdx) { dialog, which ->
                        val newStatus = when (which) {
                            0 -> TableStatus.AVAILABLE
                            1 -> TableStatus.BOOKED
                            2 -> TableStatus.OCCUPIED
                            else -> TableStatus.AVAILABLE
                        }
                        TableRepository.updateTableStatus(table.id, newStatus)
                        renderAllRooms()
                        updateSummary()

                        // Sinkronkan update status meja langsung ke backend MySQL XAMPP
                        ApiClient.updateTable(table.id, newStatus.name) { success, _ ->
                            if (success) {
                                Toast.makeText(requireContext(), "Meja ${table.id} diupdate ke ${newStatus.name}", Toast.LENGTH_SHORT).show()
                            }
                        }
                        dialog.dismiss()
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }

            grid.addView(itemView)
        }
    }

    private fun updateSummary() {
        val available = TableRepository.getAvailableCount()
        val booked = TableRepository.getAllTables().count { it.status == TableStatus.BOOKED }
        val occupied = TableRepository.getAllTables().count { it.status == TableStatus.OCCUPIED }
        binding.tvFloorSummary.text = "$available Tersedia / $booked Dipesan / $occupied Terisi"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
