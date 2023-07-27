package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentProfileBinding
import com.example.myapplication.extensions.gone
import com.example.myapplication.extensions.show


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            personalinfobtn.setOnClickListener {
                personalinfo.show()
                experience.gone()
                review.gone()
                personalinfobtn.setTextColor(resources.getColor(R.color.blue))
                experiencebtn.setTextColor(resources.getColor(R.color.grey))
                reviewbtn.setTextColor(resources.getColor(R.color.grey))
            }

            experiencebtn.setOnClickListener {
                personalinfo.gone()
                experience.show()
                review.gone()
                personalinfobtn.setTextColor(resources.getColor(R.color.grey))
                experiencebtn.setTextColor(resources.getColor(R.color.blue))
                reviewbtn.setTextColor(resources.getColor(R.color.grey))
            }

            reviewbtn.setOnClickListener {
                personalinfo.gone()
                experience.gone()
                review.show()
                personalinfobtn.setTextColor(resources.getColor(R.color.grey))
                experiencebtn.setTextColor(resources.getColor(R.color.grey))
                reviewbtn.setTextColor(resources.getColor(R.color.blue))
            }
        }

        //binding.personalinfobtn.performClick()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}