package com.flinders.coffee.ui.owner

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.flinders.coffee.R
import com.flinders.coffee.data.FinanceRepository
import com.flinders.coffee.data.TableRepository
import com.flinders.coffee.databinding.FragmentOwnerHomeBinding
import com.flinders.coffee.model.TableStatus
import java.text.NumberFormat
import java.util.Locale

class OwnerHomeFragment : Fragment() {

    private var _binding: FragmentOwnerHomeBinding? = null
    private val binding get() = _binding!!

    private val rupiahFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID")).apply {
        maximumFractionDigits = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOwnerHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardHomeNetProfit.setOnClickListener {
            startActivity(Intent(requireContext(), OwnerReportActivity::class.java))
        }

        binding.btnQuickStamp.setOnClickListener {
            val intent = Intent(requireContext(), OwnerConfirmStampActivity::class.java)
            startActivity(intent)
        }

        binding.btnQuickBooking.setOnClickListener {
            (activity as? OwnerMainActivity)?.switchToTab(R.id.nav_owner_order)
        }

        binding.btnQuickReport.setOnClickListener {
            val intent = Intent(requireContext(), OwnerReportActivity::class.java)
            startActivity(intent)
        }

        binding.tvSeeAllActivity.setOnClickListener {
            (activity as? OwnerMainActivity)?.switchToTab(R.id.nav_owner_order)
        }

        loadRealData()
    }

    override fun onResume() {
        super.onResume()
        loadRealData()
    }

    private fun loadRealData() {
        updateDashboardFinance()
        FinanceRepository.syncFromApi {
            if (_binding != null) {
                updateDashboardFinance()
            }
        }
        TableRepository.syncFromApi {
            if (_binding != null) {
                updateDashboardFinance()
            }
        }
    }

    private fun updateDashboardFinance() {
        val totalSales = FinanceRepository.monthlySales
        val totalHpp = FinanceRepository.monthlyHpp
        val totalExpense = FinanceRepository.getTotalExpenses()
        val netProfit = FinanceRepository.getNetProfit()

        binding.tvHomeNetProfit.text = rupiahFormat.format(netProfit)
        binding.tvHomeProfitFormula.text =
            "Penjualan ${rupiahFormat.format(totalSales)} − HPP ${rupiahFormat.format(totalHpp)} − pengeluaran ${rupiahFormat.format(totalExpense)}"

        val bookedTables = TableRepository.getAllTables().count { it.status == TableStatus.BOOKED }
        binding.tvActiveBookings.text = "$bookedTables"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
