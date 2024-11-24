package com.example.culturlens.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.culturlens.R
import com.example.culturlens.SettingPreferences
import com.example.culturlens.SettingPreferencesViewModel
import com.example.culturlens.SettingsViewModelFactory
import com.example.culturlens.ui.login.WelcomeActivity
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

        val btnLogout = view.findViewById<ImageButton>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        val btnEditProfile = view.findViewById<Button>(R.id.btnEditProfile)
        btnEditProfile.setOnClickListener {
            // Navigasi ke EditProfileFragment
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        return view
    }

    private fun showLogoutConfirmationDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes, log me out") { _, _ ->
                logout()
            }
            .setNegativeButton("No, keep me signed in", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.celadon_blue)
            )
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.celadon_blue)
            )
        }

        dialog.show()
    }

        private fun logout() {
        // Mengarahkan pengguna ke WelcomeActivity
        val intent = Intent(requireContext(), WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}