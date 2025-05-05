package com.example.artbooknavfragment.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.artbooknavfragment.R
import com.example.artbooknavfragment.databinding.FragmentBeginBinding

class BeginFragment : Fragment() {
    var _binding : FragmentBeginBinding? = null
    val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBeginBinding.inflate(inflater , container , false)
        val view = binding.root
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.beginButton.setOnClickListener {

            findNavController().navigate(R.id.action_beginFragment_to_artFragment)

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}