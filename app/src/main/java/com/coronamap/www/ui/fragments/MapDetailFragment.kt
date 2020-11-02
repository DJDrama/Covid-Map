package com.coronamap.www.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.coronamap.www.R
import com.coronamap.www.databinding.FragmentMapDetailBinding

class MapDetailFragment : Fragment(R.layout.fragment_map_detail) {

    private var _binding: FragmentMapDetailBinding? = null
    private val binding
        get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMapDetailBinding.bind(view)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

}