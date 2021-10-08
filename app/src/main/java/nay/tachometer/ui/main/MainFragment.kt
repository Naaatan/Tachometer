package nay.tachometer.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import nay.tachometer.R
import nay.tachometer.databinding.MainFragmentBinding
import nay.tachometer.setting.SettingTachometer
import nay.tachometer.setting.settingTachometer

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = MainFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupSettings(binding)

        /*
        // ViewBinding Code
        viewModel.meterValue.onEach {
            binding.tachometer.setMeterValue(it, 1000L) {
                // called when the animation ends
            }
        }.launchIn(lifecycleScope)
        */

        return binding.root
    }

    private fun setupSettings(binding: MainFragmentBinding) {
        for (setting in settingTachometer()) {
            val chip = Chip(requireContext())
            chip.id = setting.id

            when (setting) {
                is SettingTachometer.HexFormat -> {
                    chip.setChipDrawable(
                        ChipDrawable.createFromAttributes(
                            requireContext(),
                            null,
                            0,
                            R.style.Widget_MaterialComponents_Chip_Filter
                        )
                    )
                    chip.text = "HexFormat"
                    chip.chipBackgroundColor = context?.getColorStateList(R.color.design_default_color_secondary)
                    chip.setOnCheckedChangeListener { _, b ->
                        if (b) {
                            binding.tachometer.setValueFormatter(setting.formatter)
                        } else {
                            binding.tachometer.setValueFormatter(null)
                        }
                    }
                }
            }
            binding.tachometerSettingCG.addView(chip)
        }
    }
}