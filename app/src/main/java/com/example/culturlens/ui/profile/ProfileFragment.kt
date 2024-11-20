package com.example.culturlens.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.culturlens.R
import com.example.culturlens.SettingPreferences
import com.example.culturlens.SettingPreferencesViewModel
import com.example.culturlens.SettingsViewModelFactory
import com.google.android.material.switchmaterial.SwitchMaterial

class ProfileFragment : Fragment() {

    private val viewModel: SettingPreferencesViewModel by viewModels {
        SettingsViewModelFactory(SettingPreferences(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val switchTheme = view.findViewById<SwitchMaterial>(R.id.switch_theme)

        viewModel.getThemeSetting().observe(viewLifecycleOwner) { isDarkModeActive ->
            switchTheme.isChecked = isDarkModeActive
        }

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveThemeSetting(isChecked)
        }

        return view
    }
}