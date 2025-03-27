package com.test.fleettrack.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.test.fleettrack.R
import com.test.fleettrack.data.ViewModelFactory
import com.test.fleettrack.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels {
        ViewModelFactory.getInstance(
            requireActivity()
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getData().observe(requireActivity()) {
            if (it != null) {
                if (it.engineStatus) {
                    binding.cardEngine.itemDesc.text = getString(R.string.on)
                    binding.cardEngine.itemDesc.setTextColor(requireActivity().getColor(R.color.green))
                } else {
                    binding.cardEngine.itemDesc.text = getString(R.string.off)
                    binding.cardEngine.itemDesc.setTextColor(requireActivity().getColor(R.color.red))
                }

                if (it.doorStatus) {
                    binding.cardDoor.itemDesc.text = getString(R.string.closed)
                    binding.cardDoor.itemDesc.setTextColor(requireActivity().getColor(R.color.green))
                } else {
                    binding.cardDoor.itemDesc.text = getString(R.string.open)
                    binding.cardDoor.itemDesc.setTextColor(requireActivity().getColor(R.color.red))
                }
                binding.cardSpeed.itemDesc.text = String.format(".%2f Kmph", it.speed)
            }

        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}