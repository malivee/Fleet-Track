package com.test.fleettrack.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.test.fleettrack.ui.MapsActivity
import com.test.fleettrack.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root



        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnMap.setOnClickListener {
            if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                val intent = Intent(requireActivity(), MapsActivity::class.java)
                requireActivity().startActivity(intent)
            } else {
                Toast.makeText(
                    requireActivity(),
                    "Need Location Permission for This Action",
                    Toast.LENGTH_SHORT
                ).show()
                permissionsLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }

        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        when {
            it[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
            }

            it[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
            }

            else -> {
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}