package com.coronamap.www.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.coronamap.www.R
import com.coronamap.www.api.CoronaApi
import com.coronamap.www.databinding.FragmentDashBoardBinding
import com.coronamap.www.databinding.FragmentMapDetailBinding
import com.coronamap.www.model.LocalCounter

class DashBoardFragment : Fragment(R.layout.fragment_dash_board) {

    private val api by lazy {
        CoronaApi.create()
    }
    private val viewModel by viewModels<DashBoardViewModel> { DashBoardViewModelFactory(api) }

    private var _binding: FragmentDashBoardBinding? = null
    private val binding
        get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashBoardBinding.bind(view)
        viewModel.localCounterLiveData.observe(viewLifecycleOwner) {
            updateUI(it)
        }
    }

    private fun updateUI(localCounter: LocalCounter) {
        localCounter.apply {
            binding.tvTotalcase.text = totalCase.plus("명")
            binding.tvTodayCaseBefore.text = "(전날대비: ".plus(totalCaseBefore).plus("명").plus(")")
            binding.tvTotalDeath.text = totalDeath.plus("명")
            binding.tvTotalRecovered.text = totalRecovered.plus("명")
            binding.tvTotalQurantine.text = nowCase.plus("명")

            binding.tvRecoverPercentage.text = "(국내 완치율 : ".plus(recoveredPercentage).plus("%)")
            binding.tvDeathPercentage.text = "(국내 사망률 : ".plus(recoveredPercentage).plus("%)")

            binding.tvCheckingCounter.text = checkingCounter.plus("명")
            binding.tvCheckingPercentage.text = "(".plus(checkingPercentage).plus("%)")

            binding.tvCaseCount.text = caseCount.plus("명")
            binding.tvCasePercentage.text = "(".plus(casePercentage).plus("%)")

            binding.tvNotCaseCount.text = notCaseCount.plus("명")
            binding.tvNotCaseCountPercentage.text = "(".plus(notCasePercentage).plus("%)")
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}