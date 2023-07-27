package com.example.myapplication.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.databinding.FragmentMainBinding
import com.example.myapplication.service.LocationService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                if (it.key == Manifest.permission.ACCESS_FINE_LOCATION) {
                    startLocation(it.value)
                }
            }
        }

    private fun startLocation(value: @JvmSuppressWildcards Boolean) {
        if (value) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requireContext().startForegroundService(
                    Intent(
                        this.context,
                        LocationService::class.java
                    )
                )
            } else {
                requireContext().startService(
                    Intent(
                        this.context,
                        LocationService::class.java
                    )
                )
            }
            Toast.makeText(requireContext(), "service started!", Toast.LENGTH_SHORT).show()

        } else {
            requireContext().stopService(
                Intent(
                    this.context,
                    LocationService::class.java
                )
            )

            Toast.makeText(
                requireContext(),
                "service stopped! No permission Granted!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            buttonPickImage.setOnClickListener {
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToImagePickerFragment())
            }

            buttonLogout.setOnClickListener {
                Firebase.auth.signOut()
                requireActivity().finish()
            }

            buttonProfile.setOnClickListener {
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToProfileFragment())
            }

            realtimeDataBase.setOnClickListener {
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToRealTimeDataFragment())
            }

            buttonService.setOnClickListener {
                if (LocationService.isMyServiceRunning(
                        LocationService::class.java,
                        requireContext()
                    )
                ) {
                    requireContext().stopService(
                        Intent(
                            requireContext(),
                            LocationService::class.java
                        )
                    )
                    Toast.makeText(requireContext(), "service stopped!", Toast.LENGTH_SHORT).show()
                } else {
                    startLocation(checkPermissions())
                }
            }
        }
    }

    private fun checkPermissions(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }

        val requiredPermissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.FOREGROUND_SERVICE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requiredPermissions.add(Manifest.permission.FOREGROUND_SERVICE)
            }
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requiredPermissions.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        return if (requiredPermissions.isNotEmpty()) {
            requestPermissionLauncher.launch(requiredPermissions.toTypedArray())
            false
        } else {
            true
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}