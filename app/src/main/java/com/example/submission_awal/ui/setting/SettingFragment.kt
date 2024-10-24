package com.example.submission_awal.ui.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.submission_awal.databinding.FragmentSettingBinding


class SettingFragment : Fragment() {


    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val switchTheme = binding.switchTheme
        val reminderTheme = binding.reminderTheme

        val pref = SettingPreferences.getInstance(requireContext().dataStore)
        val settingViewModel =
            ViewModelProvider(this, SettingViewModelFactory(pref))[SettingViewModel::class.java]

        // Observe theme setting
        settingViewModel.getThemeSetting().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            switchTheme.isChecked = isDarkModeActive
        }

        // Observe notification setting
        settingViewModel.isNotificationEnabled().observe(viewLifecycleOwner) { isNotificationEnabled: Boolean ->
            reminderTheme.isChecked = isNotificationEnabled
        }

        // Handle theme switch changes
        switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            settingViewModel.saveThemeSetting(isChecked)
        }

        // Handle notification switch changes
        reminderTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            settingViewModel.saveNotificationSetting(isChecked)
        }

        binding.likedEvent.setOnClickListener {
            val action = SettingFragmentDirections.actionSettingFragmentToFavoriteFragment()
            findNavController().navigate(action)
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}